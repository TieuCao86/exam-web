package com.exam.exam_web.api.student;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class StudentCourseController {

    private final CourseService courseService;

    // (Hàm 3) NGHIEP VU SINH VIEN: Lay danh sach cac khoa hoc ma Sinh vien do tham gia
    @GetMapping
    public List<CourseDTO> getCoursesByStudent(@RequestParam String userId) {
        return courseService.findByUser(userId);
    }
}