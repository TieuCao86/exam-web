package com.exam.exam_web.api.admin;

import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.dto.PageResponse;
import com.exam.exam_web.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminExamController {

    private final ExamService examService;

    @GetMapping("/page")
    public ResponseEntity<PageResponse<ExamDTO>> getExamsPaged(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {
        // Gọi đến hàm findAllPaged(keyword, page) mang kích thước size = 12 cứng ở Service
        return ResponseEntity.ok(examService.findAllPaged(keyword, page));
    }

    @GetMapping
    public List<ExamDTO> getAll() {
        return examService.findAll();
    }
}