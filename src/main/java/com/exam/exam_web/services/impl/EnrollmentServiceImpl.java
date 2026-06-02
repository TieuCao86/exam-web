package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.EnrollmentDTO;
import com.exam.exam_web.entity.*;
import com.exam.exam_web.mapper.EnrollmentMapper;
import com.exam.exam_web.repository.EnrollmentRepository;
import com.exam.exam_web.services.EnrollmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;

    public EnrollmentServiceImpl(
            EnrollmentRepository enrollmentRepository,
            EnrollmentMapper enrollmentMapper
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentMapper = enrollmentMapper;
    }

    // ================= CREATE =================

    @Override
    public EnrollmentDTO createEnrollment(EnrollmentDTO dto) {

        requireAdminOrTeacher();

        boolean exists = enrollmentRepository
                .existsByUser_UserIdAndCourse_CourseId(
                        dto.getUserId(),
                        dto.getCourseId()
                );

        if (exists) {
            throw new RuntimeException("Already enrolled");
        }

        Enrollment enrollment = enrollmentMapper.toEntity(dto);

        enrollment.setStatus(EnrollmentStatus.PENDING);
        enrollment.setEnrolledAt(LocalDateTime.now());

        return enrollmentMapper.toDTO(enrollmentRepository.save(enrollment));
    }

    // ================= UPDATE =================

    @Override
    public EnrollmentDTO updateEnrollment(EnrollmentDTO dto) {

        requireAdminOrTeacher();

        Enrollment existing = enrollmentRepository.findById(dto.getEnrollmentId())
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        existing.setStatus(dto.getStatus());

        return enrollmentMapper.toDTO(enrollmentRepository.save(existing));
    }

    // ================= DELETE =================

    @Override
    public boolean deleteEnrollment(String id) {

        requireAdminOrTeacher();

        if (!enrollmentRepository.existsById(id)) return false;

        enrollmentRepository.deleteById(id);
        return true;
    }

    // ================= QUERY =================

    @Override
    public EnrollmentDTO findById(String id) {
        return enrollmentRepository.findById(id)
                .map(enrollmentMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<EnrollmentDTO> findAll() {
        return enrollmentRepository.findAll()
                .stream()
                .map(enrollmentMapper::toDTO)
                .toList();
    }

    @Override
    public List<EnrollmentDTO> findByUser(String userId) {
        return enrollmentRepository.findByUser_UserId(userId)
                .stream()
                .map(enrollmentMapper::toDTO)
                .toList();
    }

    @Override
    public List<EnrollmentDTO> findByStudent(String userId) {
        return enrollmentRepository.findByUser_UserId(userId)
                .stream()
                .map(enrollmentMapper::toDTO)
                .toList();
    }

    @Override
    public List<EnrollmentDTO> findByCourse(String courseId) {
        return enrollmentRepository.findByCourse_CourseId(courseId)
                .stream()
                .map(enrollmentMapper::toDTO)
                .toList();
    }

    // ================= APPROVE =================

    @Override
    public boolean approveEnrollment(String id) {

        requireAdminOrTeacher();

        Enrollment e = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (e.getStatus() == null || e.getStatus() != EnrollmentStatus.PENDING) {
            return false;
        }

        e.setStatus(EnrollmentStatus.APPROVED);
        enrollmentRepository.save(e);

        return true;
    }

    @Override
    public boolean rejectEnrollment(String enrollmentId) {

        requireAdminOrTeacher();

        Enrollment e = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        if (e.getStatus() != EnrollmentStatus.PENDING) {
            return false;
        }

        e.setStatus(EnrollmentStatus.REJECTED);
        enrollmentRepository.save(e);

        return true;
    }

    @Override
    public boolean isEnrolled(String userId, String courseId) {
        return enrollmentRepository
                .existsByUser_UserIdAndCourse_CourseId(userId, courseId);
    }

    // ================= REJECT =================

    @Override
    public boolean cancelEnrollment(String id) {

        requireAdminOrTeacher();

        Enrollment e = enrollmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        e.setStatus(EnrollmentStatus.REJECTED);
        enrollmentRepository.save(e);

        return true;
    }

    // ================= CHECK =================

    @Override
    public boolean checkEnrollment(String userId, String courseId) {
        return enrollmentRepository
                .existsByUser_UserIdAndCourse_CourseId(userId, courseId);
    }

    @Override
    public Set<String> getRejectedCourseIds(String userId) {
        return enrollmentRepository
                .findByUser_UserIdAndStatus(userId, EnrollmentStatus.REJECTED)
                .stream()
                .map(e -> e.getCourse().getCourseId())
                .collect(Collectors.toSet());
    }

    // ================= SECURITY =================

    private void requireAdminOrTeacher() {
        // Spring Security later
    }
}