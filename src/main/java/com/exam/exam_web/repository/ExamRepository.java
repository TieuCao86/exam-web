package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    Optional<Exam> findByIdWithCourse(String examId);

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.userId = :userId
        AND :now BETWEEN e.openDate AND e.closeDate
    """)
    List<Exam> findAvailableExams(String userId, LocalDateTime now);

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.userId = :userId
        AND e.openDate > :now
    """)
    List<Exam> findUpcomingExams(String userId, LocalDateTime now);

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.userId = :userId
        AND e.closeDate < :now
    """)
    List<Exam> findExpiredExams(String userId, LocalDateTime now);

    // ================= PARTICIPATION =================

    @Query("""
        SELECT COUNT(h) > 0
        FROM ExamHistory h
        WHERE h.user.userId = :userId
        AND h.exam.examId = :examId
    """)
    boolean checkParticipate(String userId, String examId);

    // ================= USER EXAMS =================

    @Query("""
        SELECT e
        FROM Exam e
        JOIN e.course c
        JOIN c.enrollments en
        WHERE en.user.userId = :userId
    """)
    List<Exam> findAllByUserId(String userId);
}