package com.exam.exam_web.api.student;

import com.exam.exam_web.dto.*;
import com.exam.exam_web.services.ExamHistoryService;
import com.exam.exam_web.services.ExamService;
import com.exam.exam_web.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class StudentExamController {

    private final ExamService examService;
    private final QuestionService questionService;
    private final ExamHistoryService examHistoryService;

    // Xem cấu hình tổng quan của một đề thi trước khi bấm vào thi
    @GetMapping("/{examId}")
    public ExamDTO getById(@PathVariable String examId) {
        return examService.findById(examId);
    }

    // Xác thực mật khẩu phòng máy
    @PostMapping("/{examId}/verify-password")
    public ResponseEntity<Boolean> verifyExamPassword(
            @PathVariable String examId,
            @RequestParam String password) {
        return ResponseEntity.ok(examService.verifyPassword(examId, password));
    }

    // Kiểm tra điều kiện (Check số lượt làm bài, thời gian mở đề)
    @GetMapping("/{examId}/check-eligibility")
    public ResponseEntity<Boolean> checkEligibility(
            @PathVariable String examId,
            @RequestParam String userId) {
        return ResponseEntity.ok(examHistoryService.checkEligibility(userId, examId));
    }

    // Lấy đề thi an toàn (Ẩn đáp án đúng) để hiển thị lên màn hình làm bài
    @GetMapping("/{examId}/questions")
    public List<ExamQuestionDTO> getQuestions(@PathVariable String examId) {
        return questionService.getExamQuestions(examId);
    }

    // Đồng bộ thời gian thực tế đề phòng sập nguồn, F5 rời tab
    @GetMapping("/{examId}/time-left")
    public int getRemainingSeconds(
            @PathVariable String examId,
            @RequestParam String userId) {
        return examHistoryService.getRemainingSeconds(userId, examId);
    }

    // Sinh viên bấm Start (Hệ thống ghi nhận mốc thời gian bắt đầu)
    @PostMapping("/{examId}/start")
    public ExamAttemptHistoryDTO startExam(
            @PathVariable String examId,
            @RequestParam String userId) {
        return examHistoryService.startExam(userId, examId);
    }

    // Ghi nhận hành vi gian lận (Rời tab/Thoát toàn màn hình)
    @PostMapping("/{examId}/cheat-report")
    public void reportCheat(
            @PathVariable String examId,
            @RequestParam String userId) {
        examHistoryService.increaseCheatCount(userId, examId);
    }

    // Bấm nộp bài (Hệ thống tự động chấm điểm trên Server và trả kết quả lập tức)
    // 8. Nộp bài và chấm điểm thực tế tại Server dựa trên questionId và answerId
    @PostMapping("/{examId}/submit")
    public ExamAttemptResultDTO submitExam(
            @PathVariable String examId,
            @RequestParam String userId,
            @RequestBody ExamSubmitBody body
    ) {
        return examHistoryService.submitExam(
                examId,
                userId,
                body
        );
    }

    // Xem danh sách tóm tắt lịch sử thi ở trang cá nhân của Sinh viên
    @GetMapping("/history")
    public List<ExamHistorySummaryDTO> getStudentHistory(@RequestParam String userId) {
        return examHistoryService.findHistoryByUser(userId);
    }

    // Xem chi tiết các lượt làm bài của một đề (Lần 1, Lần 2...)
    @GetMapping("/{examId}/attempts")
    public List<ExamAttemptHistoryDTO> getExamAttempts(
            @PathVariable String examId,
            @RequestParam String userId) {
        return examHistoryService.findAttempts(userId, examId);
    }

    // Xem lại bài làm cũ (Trang Review xem câu nào đúng, câu nào sai)
    @GetMapping("/history/{examHistoryId}/review")
    public ExamAttemptResultDTO reviewExamAttempt(@PathVariable String examHistoryId) {
        return examHistoryService.findAttemptResult(examHistoryId);
    }

    @lombok.Data
    public static class ExamSubmitBody {
        private int elapsedSeconds;
        private List<AnswerSelectionDTO> userAnswers;
    }
}