package com.exam.exam_web.services;

import com.exam.exam_web.dto.ExamAttemptHistoryDTO;
import com.exam.exam_web.dto.ExamAttemptResultDTO;
import com.exam.exam_web.dto.ExamHistorySummaryDTO;

import java.util.List;

public interface ExamHistoryService {

    // Trang /history
    List<ExamHistorySummaryDTO> findHistoryByUser(String userId);

    // Trang /history/{examId}
    List<ExamAttemptHistoryDTO> findAttempts(
            String userId,
            String examId
    );

    // Trang /history/attempt/{historyId}
    ExamAttemptResultDTO findAttemptResult(
            String examHistoryId
    );

    Integer getMaxAttemptNumber(
            String userId,
            String examId
    );

    ExamAttemptResultDTO submitExam(
            String userId,
            String examId,
            int[] selectedIndexes,
            int elapsedSeconds
    );

    ExamAttemptHistoryDTO startExam(String userId, String examId);

}