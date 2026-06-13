package com.exam.exam_web.api.student;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.PageResponse;
import com.exam.exam_web.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/student/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class StudentCourseController {

    private final CourseService courseService;

    @GetMapping("/page")
    public ResponseEntity<PageResponse<CourseDTO>> getCoursesByStudentPaged(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page
    ) {
        // Gọi xuống hàm Service bọc cứng size = 12 đã cấu hình ở các bước trước
        return ResponseEntity.ok(courseService.getCoursesByStudent(userId, page, 12));
    }

    @GetMapping
    public List<CourseDTO> getCoursesByStudent(@RequestParam String userId) {
        return courseService.findByUser(userId);
    }
}