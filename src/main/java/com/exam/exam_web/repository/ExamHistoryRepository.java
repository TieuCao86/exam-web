package com.exam.exam_web.repository;

import com.exam.exam_web.entity.ExamHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExamHistoryRepository extends JpaRepository<ExamHistory, String> {

    List<ExamHistory> findByUser_UserIdAndExam_ExamId(String userId, String examId);

    List<ExamHistory> findByUser_UserId(String userId);

    List<ExamHistory> findByExam_ExamId(String examId);

    @Query("""
        SELECT COALESCE(MAX(h.attemptNumber), 0)
        FROM ExamHistory h
        WHERE h.user.userId = :userId
        AND h.exam.examId = :examId
    """)
    int getMaxAttemptNumber(String userId, String examId);
}