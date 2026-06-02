package com.exam.exam_web.services;

import com.exam.exam_web.dto.ExamHistoryDTO;

import java.util.List;

public interface ExamHistoryService {

    List<ExamHistoryDTO>
    findByUserAndExam(
            String userId,
            String examId
    );

    List<ExamHistoryDTO>
    findByUser(
            String userId
    );

    List<ExamHistoryDTO>
    findByExam(
            String examId
    );

    int getMaxAttemptNumber(
            String userId,
            String examId
    );

    ExamHistoryDTO submitExam(
            String userId,
            String examId,
            int[] selectedIndexes,
            int elapsedSeconds
    );
}