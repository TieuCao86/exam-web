package com.exam.exam_web.api.teacher;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.PageResponse;
import com.exam.exam_web.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teacher/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TeacherCourseController {

    private final CourseService courseService;

    @GetMapping("/page")
    public ResponseEntity<PageResponse<CourseDTO>> getCoursesByTeacherPaged(
            @RequestParam String teacherId,
            @RequestParam(defaultValue = "0") int page
    ) {
        // Gọi xuống hàm Service bọc cứng size = 12 đã cấu hình ở các bước trước
        return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId, page, 12));
    }

    // NGHIEP VU GIAO VIEN: Lay danh sach cac khoa hoc do Giaovien phu trach (Không phân trang cũ)
    @GetMapping
    public List<CourseDTO> getCoursesByTeacher(@RequestParam String teacherId) {
        return courseService.findByTeacher(teacherId);
    }
}