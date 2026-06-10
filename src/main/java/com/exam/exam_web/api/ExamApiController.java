package com.exam.exam_web.api;

import com.exam.exam_web.dto.*;
import com.exam.exam_web.services.ExamHistoryService;
import com.exam.exam_web.services.ExamService;
import com.exam.exam_web.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamApiController {

    private final ExamService examService;
    private final QuestionService questionService;
    private final ExamHistoryService examHistoryService;

    // 1. Lấy danh sách tất cả đề thi hiển thị ở trang chủ Student
    @GetMapping
    public List<ExamDTO> getAll() {
        return examService.findAll();
    }

    // 2. Xem cấu hình tổng quan của một đề thi cụ thể
    @GetMapping("/{examId}")
    public ExamDTO getById(@PathVariable String examId) {
        return examService.findById(examId);
    }

    // 2.1 API Xác thực mật khẩu đề thi (Dành cho các đề thi bảo mật tại phòng máy)
    @PostMapping("/{examId}/verify-password")
    public ResponseEntity<Boolean> verifyExamPassword(
            @PathVariable String examId,
            @RequestParam String password
    ) {
        // Hãy triển khai hàm verifyPassword trong examService (so sánh password gửi lên với DB)
        boolean isValid = examService.verifyPassword(examId, password);
        return ResponseEntity.ok(isValid);
    }

    // 3. API kiểm tra điều kiện trước khi bấm nút vào thi (Tránh gian lận/quá lượt)
    @GetMapping("/{examId}/check-eligibility")
    public ResponseEntity<Boolean> checkEligibility(
            @PathVariable String examId,
            @RequestParam String userId
    ) {
        return ResponseEntity.ok(examHistoryService.checkEligibility(userId, examId));
    }

    // 4. Lấy danh sách câu hỏi an toàn (đã giấu isCorrect) để sinh viên làm bài
    @GetMapping("/{examId}/questions")
    public List<ExamQuestionDTO> getQuestions(@PathVariable String examId) {
        return questionService.getExamQuestions(examId);
    }

    // 5. API đồng bộ thời gian: Tính số giây còn lại thực tế khi FE lỡ tay F5/sập nguồn
    @GetMapping("/{examId}/time-left")
    public int getRemainingSeconds(
            @PathVariable String examId,
            @RequestParam String userId
    ) {
        return examHistoryService.getRemainingSeconds(userId, examId);
    }

    // 6. Bắt đầu làm bài (Ghi nhận mốc thời gian createdAt vào DB)
    @PostMapping("/{examId}/start")
    public ExamAttemptHistoryDTO startExam(
            @PathVariable String examId,
            @RequestParam String userId
    ) {
        return examHistoryService.startExam(userId, examId);
    }

    // 7. API báo cáo vi phạm khi sinh viên thoát chế độ Toàn màn hình (Full Screen) hoặc rời Tab
    @PostMapping("/{examId}/cheat-report")
    public void reportCheat(
            @PathVariable String examId,
            @RequestParam String userId
    ) {
        examHistoryService.increaseCheatCount(userId, examId);
    }

    // 8. Nộp bài và chấm điểm thực tế tại Server
    @PostMapping("/{examId}/submit")
    public ExamAttemptResultDTO submitExam(
            @PathVariable String examId,
            @RequestParam String userId,
            @RequestBody ExamSubmitBody body
    ) {
        return examHistoryService.submitExam(
                userId,
                examId,
                body.getSelectedIndexes(),
                body.getElapsedSeconds()
        );
    }

    // 9. Lấy danh sách tóm tắt lịch sử thi (Gom nhóm theo đề, tính điểm cao nhất)
    @GetMapping("/history")
    public List<ExamHistorySummaryDTO> getStudentHistory(@RequestParam String userId) {
        return examHistoryService.findHistoryByUser(userId);
    }

    // 9.1 API lấy chi tiết các LƯỢT THI của một đề cụ thể (Ví dụ: Lượt 1, Lượt 2, Lượt 3)
    @GetMapping("/{examId}/attempts")
    public List<ExamAttemptHistoryDTO> getExamAttempts(
            @PathVariable String examId,
            @RequestParam String userId
    ) {
        // Gọi chính xác hàm đã có sẵn trong ExamHistoryService của bạn
        return examHistoryService.findAttempts(userId, examId);
    }

    // 10. Xem lại chi tiết từng câu đúng/sai của một bài làm cũ (Trang Review)
    @GetMapping("/history/{examHistoryId}/review")
    public ExamAttemptResultDTO reviewExamAttempt(@PathVariable String examHistoryId) {
        return examHistoryService.findAttemptResult(examHistoryId);
    }

    // Class tĩnh lồng bên trong để hứng cấu trúc Body JSON từ Client gửi lên
    @lombok.Data
    public static class ExamSubmitBody {
        private int[] selectedIndexes;
        private int elapsedSeconds;
    }
}