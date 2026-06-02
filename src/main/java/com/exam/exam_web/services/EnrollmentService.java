package com.exam.exam_web.services;

import com.exam.exam_web.dto.EnrollmentDTO;

import java.util.List;
import java.util.Set;

public interface EnrollmentService {

    // ================= CRUD =================

    EnrollmentDTO createEnrollment(EnrollmentDTO dto);

    EnrollmentDTO updateEnrollment(EnrollmentDTO dto);

    boolean deleteEnrollment(String enrollmentId);

    EnrollmentDTO findById(String enrollmentId);

    List<EnrollmentDTO> findAll();

    // ================= QUERY =================

    List<EnrollmentDTO> findByUser(String userId);

    List<EnrollmentDTO> findByStudent(String userId);

    List<EnrollmentDTO> findByCourse(String courseId);

    // ================= BUSINESS =================

    boolean approveEnrollment(String enrollmentId);

    boolean rejectEnrollment(String enrollmentId);

    /**
     * Check if user already enrolled in course
     */
    boolean isEnrolled(String userId, String courseId);

    boolean cancelEnrollment(String id);

    boolean checkEnrollment(String userId, String courseId);

    /**
     * Get courses that user was rejected from
     */
    Set<String> getRejectedCourseIds(String userId);
}