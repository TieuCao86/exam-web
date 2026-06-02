package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.QuestionDTO;
import com.exam.exam_web.entity.Answer;
import com.exam.exam_web.entity.Exam;
import com.exam.exam_web.entity.Question;
import com.exam.exam_web.entity.Subject;
import com.exam.exam_web.mapper.QuestionMapper;
import com.exam.exam_web.repository.ExamRepository;
import com.exam.exam_web.repository.QuestionRepository;
import com.exam.exam_web.repository.SubjectRepository;
import com.exam.exam_web.services.QuestionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final QuestionMapper questionMapper;

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            ExamRepository examRepository,
            SubjectRepository subjectRepository,
            QuestionMapper questionMapper
    ) {
        this.questionRepository = questionRepository;
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
        this.questionMapper = questionMapper;
    }

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
        return questionRepository.findByExamId(examId)
                .stream()
                .map(questionMapper::toDTO)
                .toList();
    }

    @Override
    public List<QuestionDTO> findBySubject(String subjectId) {

        return questionRepository.findBySubject_SubjectId(subjectId)
                .stream()
                .map(questionMapper::toDTO)
                .toList();
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