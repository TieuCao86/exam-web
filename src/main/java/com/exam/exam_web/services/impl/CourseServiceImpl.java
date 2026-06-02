package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.entity.*;
import com.exam.exam_web.mapper.CourseMapper;
import com.exam.exam_web.repository.*;
import com.exam.exam_web.services.CourseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    private final CourseMapper courseMapper;

    public CourseServiceImpl(
            CourseRepository courseRepository,
            EnrollmentRepository enrollmentRepository,
            AccountRepository accountRepository,
            UserRepository userRepository, ExamRepository examRepository,
            CourseMapper courseMapper
    ) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.accountRepository = accountRepository;
        this.userRepository = userRepository;
        this.examRepository = examRepository;
        this.courseMapper = courseMapper;
    }

    // ================= SECURITY (TEMP) =================
    private void requireAdmin() {
        // TODO: Spring Security
    }

    // ================= CREATE =================
    @Override
    public CourseDTO create(CourseDTO dto) {
        requireAdmin();

        Course course = courseMapper.toEntity(dto);
        return courseMapper.toDTO(courseRepository.save(course));
    }

    // ================= UPDATE =================
    @Override
    public CourseDTO update(CourseDTO dto) {
        requireAdmin();

        Course course = courseMapper.toEntity(dto);
        return courseMapper.toDTO(courseRepository.save(course));
    }

    // ================= DELETE =================
    @Override
    public boolean delete(String courseId) {
        requireAdmin();

        if (!courseRepository.existsById(courseId)) {
            return false;
        }

        courseRepository.deleteById(courseId);
        return true;
    }

    // ================= QUERY =================
    @Override
    public List<CourseDTO> findAll() {
        return courseRepository.findAll()
                .stream()
                .map(courseMapper::toDTO)
                .toList();
    }

    @Override
    public CourseDTO findById(String courseId) {
        return courseRepository.findById(courseId)
                .map(courseMapper::toDTO)
                .orElse(null);
    }

    // ================= STUDENT COURSES =================
    @Override
    public List<CourseDTO> findByUser(String userId) {

        return enrollmentRepository.findByUser_UserId(userId)
                .stream()
                .map(e -> {
                    CourseDTO dto = courseMapper.toDTO(e.getCourse());
                    dto.setStatus(e.getStatus().name());
                    return dto;
                })
                .toList();
    }

    // ================= TEACHER COURSES =================
    @Override
    public List<CourseDTO> findByTeacher(String teacherId) {

        return courseRepository.findByTeacher_AccountId(teacherId)
                .stream()
                .map(courseMapper::toDTO)
                .toList();
    }

    // ================= EXAM -> COURSE =================
    @Override
    public CourseDTO getCourseByExamId(String examId) {

        Exam exam = examRepository.findByIdWithCourse(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        return courseMapper.toDTO(exam.getCourse());
    }

    // ================= ASSIGN TEACHER =================
    @Override
    public CourseDTO assignTeacher(String courseId, String teacherId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Account teacher = accountRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("User is not a teacher");
        }

        course.setTeacher(teacher);

        return courseMapper.toDTO(courseRepository.save(course));
    }
}