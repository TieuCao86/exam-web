package com.exam.exam_web.repository;

import com.exam.exam_web.dto.TeacherSubmissionDTO;
import com.exam.exam_web.entity.ExamHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

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
    int getMaxAttemptNumber(@Param("userId") String userId, @Param("examId") String examId);

    //Bảo mật hơn bằng cách chỉ lấy lượt thi mới nhất ĐANG LÀM (chưa có mốc submittedAt)
    @Query("""
        SELECT h FROM ExamHistory h 
        WHERE h.user.userId = :userId 
        AND h.exam.examId = :examId 
        AND h.submittedAt IS NULL
        AND h.attemptNumber = (
            SELECT COALESCE(MAX(sub.attemptNumber), 1) 
            FROM ExamHistory sub 
            WHERE sub.user.userId = :userId AND sub.exam.examId = :examId
        )
    """)
    Optional<ExamHistory> findCurrentAttempt(@Param("userId") String userId, @Param("examId") String examId);

    @Query("""
        SELECT new com.exam.exam_web.dto.TeacherSubmissionDTO(
            h.examHistoryId, 
            h.attemptNumber, 
            h.score, 
            h.elapsedSeconds, 
            h.submittedAt,
            u.userId,
            a.username,
            a.email
        )
        FROM ExamHistory h
        JOIN h.user u
        JOIN u.account a
        WHERE h.exam.examId = :examId
        ORDER BY h.submittedAt DESC, h.attemptNumber ASC
    """)
    List<TeacherSubmissionDTO> findSubmissionsByExamId(@Param("examId") String examId);
}