package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.ExamAnswerReviewDTO;
import com.exam.exam_web.dto.ExamAttemptHistoryDTO;
import com.exam.exam_web.dto.ExamAttemptResultDTO;
import com.exam.exam_web.dto.ExamHistorySummaryDTO;
import com.exam.exam_web.entity.*;
import com.exam.exam_web.mapper.ExamAttemptHistoryMapper;
import com.exam.exam_web.mapper.ExamAttemptResultMapper;
import com.exam.exam_web.mapper.ExamHistorySummaryMapper;
import com.exam.exam_web.repository.*;
import com.exam.exam_web.services.ExamHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Đã thêm import này

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExamHistoryServiceImpl implements ExamHistoryService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ExamHistoryRepository examHistoryRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final ExamQuestionRepository examQuestionRepository;

    private final ExamHistorySummaryMapper summaryMapper;
    private final ExamAttemptHistoryMapper attemptHistoryMapper;
    private final ExamAttemptResultMapper attemptResultMapper;
    private final ExamAttemptHistoryMapper examAttemptHistoryMapper;

    @Override
    public List<ExamHistorySummaryDTO> findHistoryByUser(String userId) {
        return examHistoryRepository
                .findByUser_UserId(userId)
                .stream()
                .collect(
                        java.util.stream.Collectors.groupingBy(
                                h -> h.getExam().getExamId()
                        )
                )
                .values()
                .stream()
                .map(list -> {
                    ExamHistory latest = list.stream()
                            .max((a, b) -> a.getSubmittedAt().compareTo(b.getSubmittedAt()))
                            .orElseThrow();

                    double bestScore = list.stream()
                            .mapToDouble(ExamHistory::getScore)
                            .max()
                            .orElse(0);

                    return ExamHistorySummaryDTO.builder()
                            .examId(latest.getExam().getExamId())
                            .examName(latest.getExam().getExamName())
                            .subjectName(latest.getExam().getCourse().getSubject().getName())
                            .totalAttempts((long) list.size())
                            .bestScore(bestScore)
                            .lastSubmittedAt(latest.getSubmittedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public List<ExamAttemptHistoryDTO> findAttempts(String userId, String examId) {
        return examHistoryRepository
                .findByUser_UserIdAndExam_ExamId(userId, examId)
                .stream()
                .map(attemptHistoryMapper::toDTO)
                .toList();
    }

    @Override
    public ExamAttemptResultDTO findAttemptResult(String examHistoryId) {
        ExamHistory history = examHistoryRepository.findById(examHistoryId)
                .orElseThrow(() -> new RuntimeException("History not found"));
        return attemptResultMapper.toDTO(history);
    }

    @Override
    public Integer getMaxAttemptNumber(String userId, String examId) {
        return examHistoryRepository
                .findByUser_UserIdAndExam_ExamId(userId, examId)
                .stream()
                .map(ExamHistory::getAttemptNumber)
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    @Transactional
    public ExamAttemptResultDTO submitExam(String userId, String examId, int[] selectedIndexes, int elapsedSeconds) {

        ExamHistory history = examHistoryRepository.findCurrentAttempt(userId, examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lượt làm bài hợp lệ cho sinh viên này"));

        // 2. Lấy danh sách câu hỏi của đề thi này theo đúng thứ tự orderIndex
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamExamIdOrderByOrderIndexAsc(examId);

        int totalQuestions = history.getExam().getQuestionAmount();
        int correctAnswersCount = 0;
        List<ExamAnswerReviewDTO> reviewList = new ArrayList<>();

        // 3. Duyệt qua từng câu hỏi trong đề để đối chiếu với mảng câu trả lời selectedIndexes
        for (int i = 0; i < examQuestions.size(); i++) {
            ExamQuestion eq = examQuestions.get(i);
            Question question = eq.getQuestion();
            List<Answer> answers = question.getAnswers();

            int chosenIndex = (selectedIndexes != null && i < selectedIndexes.length) ? selectedIndexes[i] : -1;

            Answer selectedAnswer = null;
            boolean isCorrect = false;
            double questionGrade = 0.0;

            if (chosenIndex >= 0 && chosenIndex < answers.size()) {
                selectedAnswer = answers.get(chosenIndex);
                if (selectedAnswer.isCorrect()) {
                    isCorrect = true;
                    correctAnswersCount++;
                    questionGrade = 10.0 / totalQuestions;
                }
            }

            String correctAnswerText = answers.stream()
                    .filter(Answer::isCorrect)
                    .map(Answer::getContent)
                    .findFirst()
                    .orElse("");

            ExamAnswer examAnswer = ExamAnswer.builder()
                    .examHistory(history)
                    .questionId(question.getQuestionId())
                    .correct(isCorrect)
                    .grade(questionGrade)
                    .questionSnapshot(question.getContent())
                    .selectedAnswerSnapshot(selectedAnswer != null ? selectedAnswer.getContent() : "Không trả lời")
                    // Đã loại bỏ hoàn toàn .orderIndex(...) hay .orderInExam(...) tại đây
                    .build();
            examAnswerRepository.save(examAnswer);

            // 5. Thêm thông tin vào danh sách trả về hiển thị kết quả (Dùng thuộc tính của DTO của bạn)
            ExamAnswerReviewDTO review = ExamAnswerReviewDTO.builder()
                    .orderInExam(i + 1) // DTO có trường này nên giữ nguyên để hiển thị ra giao diện
                    .questionContent(question.getContent())
                    .selectedAnswer(selectedAnswer != null ? selectedAnswer.getContent() : "Không trả lời")
                    .correctAnswer(correctAnswerText)
                    .correct(isCorrect)
                    .grade(questionGrade)
                    .build();
            reviewList.add(review);
        }

        // 6. Tính toán điểm số tổng hợp và làm tròn đến 2 chữ số thập phân
        double finalScore = ((double) correctAnswersCount / totalQuestions) * 10;
        finalScore = Math.round(finalScore * 100.0) / 100.0;

        // 7. Cập nhật lại bản ghi lịch sử thi
        history.setScore(finalScore);
        history.setElapsedSeconds(elapsedSeconds);
        history.setSubmittedAt(LocalDateTime.now());
        examHistoryRepository.save(history);

        // 8. Trả về thông tin đầy đủ kết quả chấm điểm
        return ExamAttemptResultDTO.builder()
                .examHistoryId(history.getExamHistoryId())
                .examName(history.getExam().getExamName())
                .attemptNumber(history.getAttemptNumber())
                .score(finalScore)
                .elapsedSeconds(elapsedSeconds)
                .submittedAt(history.getSubmittedAt())
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswersCount)
                .answers(reviewList)
                .build();
    }

    // ================= START EXAM FIXES =================
    @Override
    public ExamAttemptHistoryDTO startExam(String userId, String examId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        int nextAttempt = getMaxAttemptNumber(userId, examId) + 1;

        ExamHistory history = new ExamHistory();
        history.setUser(user);
        history.setExam(exam);
        history.setAttemptNumber(nextAttempt);
        history.setSubmittedAt(null);
        history.setScore(0.0);

        ExamHistory saved = examHistoryRepository.save(history);

        return examAttemptHistoryMapper.toDTO(saved);
    }

    private double calculateScore(String examId, int[] selectedAnswerIndexes) {
        List<Question> questions = questionRepository.findByExamId(examId);
        double score = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            Answer correct = answerRepository.findByQuestion_QuestionIdAndIsCorrectTrue(q.getQuestionId());
            List<Answer> answers = answerRepository.findByQuestion_QuestionId(q.getQuestionId());

            if (selectedAnswerIndexes[i] >= 0 && selectedAnswerIndexes[i] < answers.size()) {
                Answer selected = answers.get(selectedAnswerIndexes[i]);
                if (selected.getAnswerId().equals(correct.getAnswerId())) {
                    score += 1;
                }
            }
        }
        return score;
    }
}