package com.exam.exam_web.repository;

import com.exam.exam_web.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, String> {

    // Phương thức giúp lấy ra danh sách câu hỏi cấu hình trong đề thi
    // và tự động sắp xếp tăng dần theo thứ tự hiển thị (order_index)
    List<ExamQuestion> findByExamExamIdOrderByOrderIndexAsc(String examId);
}