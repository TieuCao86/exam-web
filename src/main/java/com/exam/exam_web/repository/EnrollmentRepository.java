package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Enrollment;
import com.exam.exam_web.entity.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, String> {

    List<Enrollment> findByUser_UserId(String userId);

    List<Enrollment> findByCourse_CourseId(String courseId);

    List<Enrollment> findByUser_UserIdAndStatus(String userId, EnrollmentStatus status);

    boolean existsByUser_UserIdAndCourse_CourseId(String userId, String courseId);
}