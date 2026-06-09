package com.exam.exam_web.api;

import com.exam.exam_web.dto.*;
import com.exam.exam_web.services.ExamHistoryService;
import com.exam.exam_web.services.ExamService;
import com.exam.exam_web.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamApiController {

    private final ExamService examService;
    private final QuestionService questionService;
    private final ExamHistoryService examHistoryService;

    @GetMapping
    public List<ExamDTO> getAll() {
        return examService.findAll();
    }

    @GetMapping("/{examId}")
    public ExamDTO getById(
            @PathVariable String examId
    ) {
        return examService.findById(examId);
    }

    @GetMapping("/{examId}/questions")
    public List<ExamQuestionDTO> getQuestions(
            @PathVariable String examId
    ) {
        return questionService.getExamQuestions(examId);
    }

    @PostMapping("/{examId}/start")
    public ExamAttemptHistoryDTO startExam(
            @PathVariable String examId,
            @RequestParam String userId
    ) {
        return examHistoryService.startExam(userId, examId);
    }

    @PostMapping("/{examId}/submit")
    public ExamAttemptResultDTO submitExam(
            @PathVariable String examId,
            @RequestParam String userId,
            @RequestBody ExamSubmitBody body
    ) {
        // Gọi chính xác phương thức theo interface ExamHistoryService của bạn
        return examHistoryService.submitExam(
                userId,
                examId,
                body.getSelectedIndexes(),
                body.getElapsedSeconds()
        );
    }

    // Class tĩnh lồng bên trong (hoặc DTO riêng biệt) để hứng cấu trúc Body JSON từ Client gửi lên
    @lombok.Data
    public static class ExamSubmitBody {
        private int[] selectedIndexes;
        private int elapsedSeconds;
    }
}