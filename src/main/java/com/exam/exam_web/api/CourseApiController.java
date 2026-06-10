package com.exam.exam_web.api;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:5173")
public class CourseApiController {

    private final CourseService courseService;

    // 1. Lay danh sach tat ca cac khoa hoc trong he thong
    @GetMapping
    public List<CourseDTO> getAll() {
        return courseService.findAll();
    }

    // 2. Tim kiem thong tin chi tiet cua mot khoa hoc theo ID
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getById(@PathVariable String courseId) {
        CourseDTO dto = courseService.findById(courseId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    // 3. NGHIEP VU SINH VIEN: Lay danh sach cac khoa hoc ma Sinh vien do tham gia
    @GetMapping("/student")
    public List<CourseDTO> getCoursesByStudent(@RequestParam String userId) {
        return courseService.findByUser(userId);
    }

    // 4. NGHIEP VU GIAO VIEN: Lay danh sach cac khoa hoc do Giaovien phu trach
    @GetMapping("/teacher")
    public List<CourseDTO> getCoursesByTeacher(@RequestParam String teacherId) {
        return courseService.findByTeacher(teacherId);
    }

    // 5. Tao moi mot khoa hoc
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDTO create(@RequestBody CourseDTO dto) {
        return courseService.create(dto);
    }

    // 6. Cap nhat thong tin khoa hoc
    @PutMapping("/{courseId}")
    public CourseDTO update(
            @PathVariable String courseId,
            @RequestBody CourseDTO dto
    ) {
        dto.setCourseId(courseId);
        return courseService.update(dto);
    }

    // 7. Xoa mot khoa hoc khoi he thong
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> delete(@PathVariable String courseId) {
        boolean isDeleted = courseService.delete(courseId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}