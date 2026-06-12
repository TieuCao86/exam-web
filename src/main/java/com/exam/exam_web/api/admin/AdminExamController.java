package com.exam.exam_web.api.admin;

import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminExamController {

    private final ExamService examService;

    // Lấy danh sách toàn bộ đề thi hiện có trên toàn hệ thống
    @GetMapping
    public List<ExamDTO> getAll() {
        return examService.findAll();
    }
}