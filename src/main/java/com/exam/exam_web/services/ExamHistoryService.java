package com.exam.exam_web.services;

import com.exam.exam_web.api.student.StudentExamController;
import com.exam.exam_web.dto.ExamAttemptHistoryDTO;
import com.exam.exam_web.dto.ExamAttemptResultDTO;
import com.exam.exam_web.dto.ExamHistorySummaryDTO;
import com.exam.exam_web.dto.TeacherSubmissionDTO;

import java.util.List;

public interface ExamHistoryService {

    List<ExamHistorySummaryDTO> findHistoryByUser(String userId);

    List<ExamAttemptHistoryDTO> findAttempts(String userId, String examId);

    ExamAttemptResultDTO findAttemptResult(String examHistoryId);

    Integer getMaxAttemptNumber(String userId, String examId);

    ExamAttemptResultDTO submitExam(String examId, String userId, StudentExamController.ExamSubmitBody body);

    List<ExamAttemptHistoryDTO> findByExamId(String examId);

    List<TeacherSubmissionDTO> getSubmissionsByExamId(String examId);

    ExamAttemptHistoryDTO startExam(String userId, String examId);

    int getRemainingSeconds(String userId, String examId);

    boolean checkEligibility(String userId, String examId);

    void increaseCheatCount(String userId, String examId);
}