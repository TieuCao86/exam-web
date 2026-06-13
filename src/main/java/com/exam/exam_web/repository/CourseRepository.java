package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Course;
import com.exam.exam_web.entity.CourseStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String> {

    Page<Course> findByTeacher_AccountId(String teacherId, Pageable pageable);

    List<Course> findBySubject_SubjectId(String subjectId);

    List<Course> findByStatus(CourseStatus status);

    List<Course> findBySemester(String semester);

    List<Course> findByAcademicYear(String academicYear);

    List<Course> findBySemesterAndAcademicYear(String semester, String academicYear);

    Page<Course> findByEnrollments_User_Account_AccountId(String studentId, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN c.exams e WHERE e.examId = :examId")
    Optional<Course> findByExamId(@Param("examId") String examId);

    @Query("""
        SELECT c
        FROM Course c
        WHERE (:keyword IS NULL OR LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:semester IS NULL OR c.semester = :semester)
          AND (:academicYear IS NULL OR c.academicYear = :academicYear)
    """)
    Page<Course> searchCourses(
            @Param("keyword") String keyword,
            @Param("semester") String semester,
            @Param("academicYear") String academicYear,
            Pageable pageable
    );
}