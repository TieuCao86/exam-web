package com.exam.exam_web.services;

import com.exam.exam_web.dto.ExamAttemptHistoryDTO;
import com.exam.exam_web.dto.ExamAttemptResultDTO;
import com.exam.exam_web.dto.ExamHistorySummaryDTO;

import java.util.List;

public interface ExamHistoryService {

    // Trang /history - Lấy danh sách tổng hợp lịch sử thi theo user
    List<ExamHistorySummaryDTO> findHistoryByUser(String userId);

    // Trang /history/{examId} - Lấy các lượt thi của 1 đề cụ thể
    List<ExamAttemptHistoryDTO> findAttempts(
            String userId,
            String examId
    );

    // Trang /history/attempt/{historyId} - Xem chi tiết kết quả lượt làm bài cũ
    ExamAttemptResultDTO findAttemptResult(
            String examHistoryId
    );

    Integer getMaxAttemptNumber(
            String userId,
            String examId
    );

    // Nộp bài và chấm điểm thực tế tại Server
    ExamAttemptResultDTO submitExam(
            String userId,
            String examId,
            int[] selectedIndexes,
            int elapsedSeconds
    );

    // Thêm kiểu import nếu cần: import com.exam.exam_web.dto.ExamAttemptHistoryDTO;
    List<ExamAttemptHistoryDTO> findByExamId(String examId);

    // Bắt đầu làm bài (Ghi nhận mốc thời gian bắt đầu)
    ExamAttemptHistoryDTO startExam(String userId, String examId);

    /**
     * API Đồng bộ thời gian: Tính số giây còn lại thực tế của lượt thi hiện tại khi FE bị F5/Sập nguồn
     */
    int getRemainingSeconds(String userId, String examId);

    /**
     * Kiểm tra điều kiện trước khi thi: Xem đề mở chưa, hết hạn chưa hoặc sinh viên đã làm quá số lượt chưa
     */
    boolean checkEligibility(String userId, String examId);

    /**
     * Ghi nhận vi phạm: Tăng số lần thoát Full Screen hoặc chuyển Tab của sinh viên
     */
    void increaseCheatCount(String userId, String examId);
}