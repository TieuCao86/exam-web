package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Course;
import com.exam.exam_web.entity.CourseStatus;
import com.exam.exam_web.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, String> {

    List<Course> findByTeacher_AccountId(String teacherId);

    List<Course> findBySubject_SubjectId(String subjectId);

    List<Course> findByStatus(CourseStatus status);

}