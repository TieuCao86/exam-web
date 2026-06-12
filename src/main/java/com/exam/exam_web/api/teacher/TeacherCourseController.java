package com.exam.exam_web.api.teacher;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teacher/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TeacherCourseController {

    private final CourseService courseService;

    // NGHIEP VU GIAO VIEN: Lay danh sach cac khoa hoc do Giaovien phu trach
    @GetMapping
    public List<CourseDTO> getCoursesByTeacher(@RequestParam String teacherId) {
        return courseService.findByTeacher(teacherId);
    }
}