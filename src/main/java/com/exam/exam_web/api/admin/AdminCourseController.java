package com.exam.exam_web.api.admin;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.PageResponse;
import com.exam.exam_web.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AdminCourseController {

    private final CourseService courseService;

    @GetMapping("/page")
    public ResponseEntity<PageResponse<CourseDTO>> getCoursesPaged(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String academicYear,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size, // Trực quan hóa giá trị 12 trên tài liệu API
            @RequestParam(defaultValue = "courseId,asc") String sortBy
    ) {
        return ResponseEntity.ok(courseService.getAllCoursesForAdmin(keyword, semester, academicYear, page, size, sortBy));
    }

    // Lấy danh sách tất cả các khóa học trong hệ thống (Không phân trang cũ)
    @GetMapping
    public List<CourseDTO> getAll() {
        return courseService.findAll();
    }

    // Tìm kiếm thông tin chi tiết khóa học
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDTO> getById(@PathVariable String courseId) {
        CourseDTO dto = courseService.findById(courseId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    // Tạo mới khóa học (Admin áp cứng)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDTO create(@RequestBody CourseDTO dto) {
        return courseService.create(dto);
    }

    // Cập nhật thông tin khóa học
    @PutMapping("/{courseId}")
    public CourseDTO update(@PathVariable String courseId, @RequestBody CourseDTO dto) {
        dto.setCourseId(courseId);
        return courseService.update(dto);
    }

    // Xóa một khóa học
    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> delete(@PathVariable String courseId) {
        boolean isDeleted = courseService.delete(courseId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}