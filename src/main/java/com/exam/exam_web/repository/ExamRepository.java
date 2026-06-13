package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, String> {

    List<Exam> findByExamNameContainingIgnoreCase(String examName);

    List<Exam> findByCourse_CourseId(String courseId);

    List<Exam> findByOpenDateBeforeAndCloseDateAfter(
            LocalDateTime now1,
            LocalDateTime now2
    );

    List<Exam> findByOpenDateAfter(LocalDateTime now);

    List<Exam> findByCloseDateBefore(LocalDateTime now);

    @Query("""
        SELECT e FROM Exam e
        JOIN FETCH e.course
        WHERE e.examId = :examId
    """)
    Optional<Exam> findByIdWithCourse(@Param("examId") String examId);

    // =====================================================================
    // 💡 PHÂN TRANG CHO ADMIN (Tìm kiếm đề thi toàn hệ thống)
    // =====================================================================
    @Query("""
        SELECT e FROM Exam e
        WHERE :keyword IS NULL OR LOWER(e.examName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<Exam> searchExams(@Param("keyword") String keyword, Pageable pageable);

    // =====================================================================
    // 💡 1. CÁC HÀM PHÂN TRANG CHO STUDENT (Trả về Page)
    // =====================================================================

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.account.accountId = :userId
        AND :now BETWEEN e.openDate AND e.closeDate
    """)
    Page<Exam> findAvailableExamsPaged(@Param("userId") String userId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.account.accountId = :userId
        AND e.openDate > :now
    """)
    Page<Exam> findUpcomingExamsPaged(@Param("userId") String userId, @Param("now") LocalDateTime now, Pageable pageable);

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.account.accountId = :userId
        AND e.closeDate < :now
    """)
    Page<Exam> findExpiredExamsPaged(@Param("userId") String userId, @Param("now") LocalDateTime now, Pageable pageable);

    // =====================================================================
    // 💡 2. CÁC HÀM CŨ KHÔNG PHÂN TRANG CHO STUDENT (Bổ sung để xóa lỗi đỏ Service)
    // =====================================================================

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.account.accountId = :userId
        AND :now BETWEEN e.openDate AND e.closeDate
    """)
    List<Exam> findAvailableExams(@Param("userId") String userId, @Param("now") LocalDateTime now);

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.account.accountId = :userId
        AND e.openDate > :now
    """)
    List<Exam> findUpcomingExams(@Param("userId") String userId, @Param("now") LocalDateTime now);

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.account.accountId = :userId
        AND e.closeDate < :now
    """)
    List<Exam> findExpiredExams(@Param("userId") String userId, @Param("now") LocalDateTime now);

    // ================= PARTICIPATION =================

    @Query("""
        SELECT COUNT(h) > 0
        FROM ExamHistory h
        WHERE h.user.account.accountId = :userId
        AND h.exam.examId = :examId
    """)
    boolean checkParticipate(@Param("userId") String userId, @Param("examId") String examId);

    // ================= USER EXAMS =================

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.account.accountId = :userId
    """)
    List<Exam> findAllByUserId(@Param("userId") String userId);

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.account.accountId = :userId
    """)
    Page<Exam> findAllByUserIdPaged(@Param("userId") String userId, Pageable pageable);
}