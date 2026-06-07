package com.exam.exam_web.services.impl;

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
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExamHistoryServiceImpl implements ExamHistoryService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;

    private final ExamHistorySummaryMapper summaryMapper;
    private final ExamAttemptHistoryMapper attemptHistoryMapper;
    private final ExamAttemptResultMapper attemptResultMapper;
    private final ExamHistoryRepository examHistoryRepository;

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
    public ExamAttemptResultDTO submitExam(String userId, String examId, int[] selectedIndexes, int elapsedSeconds) {
        int attempt = getMaxAttemptNumber(userId, examId) + 1;

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        ExamHistory history = new ExamHistory();
        history.setUser(user);
        history.setExam(exam);
        history.setAttemptNumber(attempt);
        history.setElapsedSeconds(elapsedSeconds);
        history.setSubmittedAt(LocalDateTime.now());

        double score = calculateScore(examId, selectedIndexes);
        history.setScore(score);

        ExamHistory saved = examHistoryRepository.save(history);
        return attemptResultMapper.toDTO(saved);
    }

    // ================= START EXAM FIXES =================
    @Override
    @Transactional
    public String startExam(String userId, String examId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // Dùng luôn hàm getMaxAttemptNumber có sẵn của bạn để tránh lỗi thiếu method trong repo
        int currentAttempts = getMaxAttemptNumber(userId, examId);
        int nextAttempt = currentAttempts + 1;

        ExamHistory history = ExamHistory.builder()
                .user(user)
                .exam(exam)
                .attemptNumber(nextAttempt)
                .score(0.0)
                .elapsedSeconds(0)
                .submittedAt(null)
                .build();

        ExamHistory savedHistory = examHistoryRepository.save(history);
        return savedHistory.getExamHistoryId();
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