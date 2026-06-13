package com.exam.exam_web.api.teacher;

import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.dto.PageResponse;
import com.exam.exam_web.dto.TeacherSubmissionDTO;
import com.exam.exam_web.services.ExamHistoryService;
import com.exam.exam_web.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TeacherExamController {

    private final ExamService examService;
    private final ExamHistoryService examHistoryService;

    // 1. QUẢN LÝ CẤU HÌNH ĐỀ THI (CRUD)

    @GetMapping("/page")
    public ResponseEntity<PageResponse<ExamDTO>> getExamsByTeacherPaged(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page
    ) {
        // Tận dụng luồng xử lý phân trang chung mà chúng ta vừa hoàn thiện ở Service
        return ResponseEntity.ok(examService.findAllPaged(keyword, page));
    }

    // Lấy danh sách tất cả đề thi do Giáo viên này quản lý/tạo ra (Không phân trang cũ)
    @GetMapping
    public List<ExamDTO> getExamsByTeacher(@RequestParam String teacherId) {
        return examService.findAllByTeacherId(teacherId);
    }

    // Xem chi tiết cấu hình (kèm mật khẩu) của một đề thi cụ thể
    @GetMapping("/{examId}")
    public ResponseEntity<ExamDTO> getExamDetail(@PathVariable String examId) {
        ExamDTO dto = examService.findById(examId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    // Tạo mới một đề thi
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExamDTO createExam(@RequestBody ExamDTO dto) {
        return examService.createExam(dto);
    }

    // Cập nhật cấu hình đề thi
    @PutMapping("/{examId}")
    public ExamDTO updateExam(@PathVariable String examId, @RequestBody ExamDTO dto) {
        dto.setExamId(examId);
        return examService.updateExam(dto);
    }

    // Xóa đề thi khỏi hệ thống
    @DeleteMapping("/{examId}")
    public ResponseEntity<Void> deleteExam(@PathVariable String examId) {
        boolean isDeleted = examService.deleteExam(examId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Gán một đề thi có sẵn vào một Khóa học/Lớp học cụ thể
    @PostMapping("/{examId}/assign-course")
    public ResponseEntity<Void> assignToCourse(
            @PathVariable String examId,
            @RequestParam String courseId
    ) {
        examService.assignCourse(examId, courseId);
        return ResponseEntity.ok().build();
    }

    // 2. GIÁM SÁT VÀ QUẢN LÝ KẾT QUẢ THI CỦA SINH VIÊN

    // Lấy danh sách toàn bộ bài làm, điểm số của tất cả sinh viên thuộc một đề thi
    @GetMapping("/{examId}/submissions")
    public ResponseEntity<List<TeacherSubmissionDTO>> getSubmissionsByExam(@PathVariable("examId") String examId) {
        List<TeacherSubmissionDTO> submissions = examHistoryService.getSubmissionsByExamId(examId);
        return ResponseEntity.ok(submissions);
    }
}