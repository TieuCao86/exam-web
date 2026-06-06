package com.exam.exam_web.api;

import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ExamApiController {

    private final ExamService examService;

    @GetMapping("/exams")
    public List<ExamDTO> getAll() {
        return examService.findAll();
    }

    @GetMapping("/available")
    public List<ExamDTO> getAvailable(@RequestParam String userId) {
        return examService.findAvailableExams(userId);
    }

    @GetMapping("/upcoming")
    public List<ExamDTO> getUpcoming(@RequestParam String userId) {
        return examService.findUpcomingExams(userId);
    }

    @GetMapping("/expired")
    public List<ExamDTO> getExpired(@RequestParam String userId) {
        return examService.findExpiredExams(userId);
    }
}