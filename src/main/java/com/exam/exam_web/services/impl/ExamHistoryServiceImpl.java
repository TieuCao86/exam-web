package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.ExamHistoryDTO;
import com.exam.exam_web.entity.Exam;
import com.exam.exam_web.entity.ExamHistory;
import com.exam.exam_web.entity.User;
import com.exam.exam_web.mapper.ExamHistoryMapper;
import com.exam.exam_web.repository.ExamHistoryRepository;
import com.exam.exam_web.repository.ExamRepository;
import com.exam.exam_web.repository.UserRepository;
import com.exam.exam_web.services.ExamHistoryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ExamHistoryServiceImpl implements ExamHistoryService {

    private final ExamHistoryRepository examHistoryRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final ExamHistoryMapper mapper;

    public ExamHistoryServiceImpl(
            ExamHistoryRepository examHistoryRepository, UserRepository userRepository, ExamRepository examRepository,
            ExamHistoryMapper mapper
    ) {
        this.examHistoryRepository = examHistoryRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.mapper = mapper;
    }

    // ================= QUERY =================

    @Override
    public List<ExamHistoryDTO> findByUserAndExam(String userId, String examId) {

        return examHistoryRepository
                .findByUser_UserIdAndExam_ExamId(userId, examId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamHistoryDTO> findByUser(String userId) {

        return examHistoryRepository
                .findByUser_UserId(userId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamHistoryDTO> findByExam(String examId) {

        return examHistoryRepository
                .findByExam_ExamId(examId)
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Override
    public int getMaxAttemptNumber(String userId, String examId) {
        return examHistoryRepository.getMaxAttemptNumber(userId, examId);
    }

    // ================= BUSINESS =================

    @Override
    public ExamHistoryDTO submitExam(
            String userId,
            String examId,
            int[] selectedIndexes,
            int elapsedSeconds
    ) {

        Integer max = getMaxAttemptNumber(userId, examId);
        int attempt = (max == null ? 0 : max) + 1;

        ExamHistory history = new ExamHistory();
        history.setAttemptNumber(attempt);
        history.setSubmittedAt(LocalDateTime.now());
        history.setElapsedSeconds(elapsedSeconds);

        // ✅ SAFE: verify existence
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        history.setUser(user);
        history.setExam(exam);

        // TODO: calculate score from selectedIndexes

        return mapper.toDTO(
                examHistoryRepository.save(history)
        );
    }
}