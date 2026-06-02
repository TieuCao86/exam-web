package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubjectRepository
        extends JpaRepository<Subject, String> {

    Subject findByNameIgnoreCase(
            String name
    );

    @Query("""
        SELECT s.image
        FROM Course c
        JOIN c.subject s
        WHERE c.courseId = :courseId
    """)
    String getSubjectImageByCourseId(
            String courseId
    );
}