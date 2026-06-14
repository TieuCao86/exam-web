package com.exam.exam_web.api.teacher;

import com.exam.exam_web.dto.PageResponse;
import com.exam.exam_web.dto.QuestionDTO;
import com.exam.exam_web.services.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teacher/questions")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class TeacherQuestionController {

    private final QuestionService questionService;

    @GetMapping("/subject/{subjectId}/page")
    public ResponseEntity<PageResponse<QuestionDTO>> getQuestionsBySubjectPaged(
            @PathVariable String subjectId,
            @RequestParam(defaultValue = "0") int page
    ) {
        // Sau này bạn viết hàm nhận Pageable trong QuestionService để bọc PageResponse tại đây:
         return ResponseEntity.ok(questionService.findBySubjectPaged(subjectId, page, 12));
    }

    // 1. Lấy tất cả câu hỏi thuộc một Môn học (Không phân trang cũ)
    @GetMapping("/subject/{subjectId}")
    public List<QuestionDTO> getQuestionsBySubject(@PathVariable String subjectId) {
        return questionService.findBySubject(subjectId);
    }

    // 2. Xem chi tiết 1 câu hỏi (Kèm danh sách đáp án)
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDTO> getQuestionDetail(@PathVariable String questionId) {
        QuestionDTO dto = questionService.findById(questionId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    // 3. Thêm một câu hỏi mới vào Ngân hàng đề
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuestionDTO createQuestion(@RequestBody QuestionDTO dto) {
        return questionService.createQuestion(dto);
    }

    // 4. Sửa đổi nội dung câu hỏi hoặc cập nhật lại đáp án đúng/sai
    @PutMapping("/{questionId}")
    public QuestionDTO updateQuestion(
            @PathVariable String questionId,
            @RequestBody QuestionDTO dto
    ) {
        dto.setQuestionId(questionId);
        return questionService.updateQuestion(dto);
    }

    // 5. Xóa câu hỏi khỏi ngân hàng đề
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable String questionId) {
        boolean isDeleted = questionService.deleteQuestion(questionId);
        return isDeleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}