package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.ExamOptionDTO;
import com.exam.exam_web.dto.ExamQuestionDTO;
import com.exam.exam_web.dto.PageResponse;
import com.exam.exam_web.dto.QuestionDTO;
import com.exam.exam_web.entity.Answer;
import com.exam.exam_web.entity.Exam;
import com.exam.exam_web.entity.Question;
import com.exam.exam_web.entity.Subject;
import com.exam.exam_web.mapper.AnswerMapper;
import com.exam.exam_web.mapper.QuestionMapper;
import com.exam.exam_web.repository.AnswerRepository;
import com.exam.exam_web.repository.ExamRepository;
import com.exam.exam_web.repository.QuestionRepository;
import com.exam.exam_web.repository.SubjectRepository;
import com.exam.exam_web.services.QuestionService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionMapper questionMapper;
    private final AnswerMapper answerMapper;
    private final AnswerRepository answerRepository;

    // ================= SECURITY =================

    private void requireAdminOrTeacher() {
        // TODO integrate Spring Security
    }

    // ================= CREATE =================

    @Override
    public QuestionDTO createQuestion(QuestionDTO dto) {

        requireAdminOrTeacher();

        Question question = questionMapper.toEntity(dto);

        if (dto.getSubjectId() != null && !dto.getSubjectId().isBlank()) {
            Subject subject = subjectRepository.findById(dto.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));
            question.setSubject(subject);
        }

        return questionMapper.toDTO(
                questionRepository.save(question)
        );
    }

    // ================= UPDATE =================

    @Override
    public QuestionDTO updateQuestion(QuestionDTO dto) {

        requireAdminOrTeacher();

        Question existing = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new RuntimeException("Question not found"));

        existing.setContent(dto.getContent());

        if (dto.getSubjectId() != null) {
            if (dto.getSubjectId().isBlank()) {
                existing.setSubject(null);
            } else {
                Subject subject = subjectRepository.findById(dto.getSubjectId())
                        .orElseThrow(() -> new RuntimeException("Subject not found"));
                existing.setSubject(subject);
            }
        }

        return questionMapper.toDTO(
                questionRepository.save(existing)
        );
    }

    // ================= DELETE =================

    @Override
    public boolean deleteQuestion(String questionId) {

        requireAdminOrTeacher();

        if (!questionRepository.existsById(questionId)) {
            return false;
        }

        questionRepository.deleteById(questionId);
        return true;
    }

    // ================= QUERY =================

    @Override
    public QuestionDTO findById(String questionId) {

        return questionRepository.findById(questionId)
                .map(questionMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<QuestionDTO> findAll() {

        return questionRepository.findAll()
                .stream()
                .map(questionMapper::toDTO)
                .toList();
    }

    @Override
    public List<QuestionDTO> findByContent(String keyword) {

        return questionRepository.findByContentContainingIgnoreCase(keyword)
                .stream()
                .map(questionMapper::toDTO)
                .toList();
    }

    @Override
    public List<QuestionDTO> findByExam(String examId) {

        List<Question> questions =
                questionRepository.findByExamId(examId);

        return questions.stream()
                .map(q -> {

                    QuestionDTO dto =
                            questionMapper.toDTO(q);

                    dto.setAnswers(
                            answerRepository
                                    .findByQuestion_QuestionId(
                                            q.getQuestionId()
                                    )
                                    .stream()
                                    .map(answerMapper::toDTO)
                                    .toList()
                    );

                    return dto;
                })
                .toList();
    }

    @Override
    public List<QuestionDTO> findBySubject(String subjectId) {

        return questionRepository.findBySubject_SubjectId(subjectId)
                .stream()
                .map(questionMapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamQuestionDTO> getExamQuestions(
            String examId
    ) {

        List<Question> questions =
                questionRepository.findByExamId(examId);

        List<ExamQuestionDTO> result =
                new ArrayList<>();

        int order = 1;

        for (Question question : questions) {

            List<ExamOptionDTO> options =
                    answerRepository
                            .findByQuestion_QuestionId(
                                    question.getQuestionId()
                            )
                            .stream()
                            .map(answer ->
                                    ExamOptionDTO.builder()
                                            .answerId(answer.getAnswerId())
                                            .content(answer.getContent())
                                            .build()
                            )
                            .toList();

            result.add(
                    ExamQuestionDTO.builder()
                            .questionId(question.getQuestionId())
                            .orderInExam(order++)
                            .content(question.getContent())
                            .answers(options)
                            .build()
            );
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<QuestionDTO> findBySubjectPaged(String subjectId, int page, int size) {

        // 1. Khởi tạo Pageable ép cứng cỡ trang (12 bản ghi), sắp xếp tăng dần theo ID câu hỏi
        Pageable pageable = PageRequest.of(page, size, Sort.by("questionId").ascending());

        // 2. Gọi xuống Repo lấy dữ liệu dạng Page phân mảnh dưới DB
        Page<Question> questionPage = questionRepository.findBySubject_SubjectId(subjectId, pageable);

        // 3. Chuyển đổi danh sách Entity con sang DTO bằng Mapper giống như hàm findBySubject cũ của bạn
        List<QuestionDTO> dtoList = questionPage.getContent().stream()
                .map(questionMapper::toDTO)
                .toList();

        // 4. Đóng gói trọn vẹn vào khuôn PageResponse để đẩy về cho Controller
        return PageResponse.<QuestionDTO>builder()
                .content(dtoList)
                .pageNumber(questionPage.getNumber())
                .pageSize(questionPage.getSize())
                .totalElements(questionPage.getTotalElements())
                .totalPages(questionPage.getTotalPages())
                .isLast(questionPage.isLast())
                .build();
    }

    private List<Answer> buildAnswers(QuestionDTO dto, Question question) {

        if (dto.getAnswers() == null) {
            return new ArrayList<>();
        }

        return dto.getAnswers().stream().map(a -> {
            Answer answer = new Answer();
            answer.setContent(a.getContent());
            answer.setCorrect(a.isCorrect());
            answer.setQuestion(question);
            return answer;
        }).toList();
    }
}