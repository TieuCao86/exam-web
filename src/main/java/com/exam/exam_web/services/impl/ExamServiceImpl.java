package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.entity.Course;
import com.exam.exam_web.entity.Exam;
import com.exam.exam_web.entity.Subject;
import com.exam.exam_web.entity.User;
import com.exam.exam_web.mapper.ExamMapper;
import com.exam.exam_web.repository.CourseRepository;
import com.exam.exam_web.repository.ExamRepository;
import com.exam.exam_web.repository.SubjectRepository;
import com.exam.exam_web.services.ExamService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    private final ExamMapper examMapper;

    public ExamServiceImpl(
            ExamRepository examRepository,
            SubjectRepository subjectRepository,
            CourseRepository courseRepository,
            ExamMapper examMapper
    ) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
        this.courseRepository = courseRepository;
        this.examMapper = examMapper;
    }

    // ================= CRUD =================

    @Override
    public ExamDTO createExam(ExamDTO dto) {

        requireAdminOrTeacher();

        Exam exam = examMapper.toEntity(dto);

        exam.setCreatedAt(LocalDateTime.now());
        exam.setUpdatedAt(LocalDateTime.now());

        applyRelations(exam, dto);

        return examMapper.toDTO(examRepository.save(exam));
    }

    @Override
    public ExamDTO updateExam(ExamDTO dto) {

        requireAdminOrTeacher();

        Exam existing = examRepository.findById(dto.getExamId())
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        examMapper.updateEntity(dto, existing);

        existing.setUpdatedAt(LocalDateTime.now());

        applyRelations(existing, dto);

        return examMapper.toDTO(examRepository.save(existing));
    }

    @Override
    public boolean deleteExam(String examId) {

        requireAdminOrTeacher();

        if (!examRepository.existsById(examId)) return false;

        examRepository.deleteById(examId);
        return true;
    }

    @Override
    public ExamDTO findById(String examId) {
        return examRepository.findById(examId)
                .map(examMapper::toDTO)
                .orElse(null);
    }

    // ================= QUERIES =================

    @Override
    public List<ExamDTO> findAll() {
        return examRepository.findAll()
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamDTO> findByName(String name) {
        return examRepository.findByExamNameContainingIgnoreCase(name)
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamDTO> findByCourse(String courseId) {
        return examRepository.findByCourse_CourseId(courseId)
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    // ================= STATUS =================

    @Override
    public List<ExamDTO> findAvailableExams() {
        LocalDateTime now = LocalDateTime.now();

        return examRepository
                .findByOpenDateBeforeAndCloseDateAfter(now, now)
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamDTO> findUpcomingExams() {
        return examRepository.findByOpenDateAfter(LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamDTO> findExpiredExams() {
        return examRepository.findByCloseDateBefore(LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamDTO> findAvailableExams(String userId) {
        return List.of();
    }

    @Override
    public List<ExamDTO> findUpcomingExams(String userId) {
        return List.of();
    }

    @Override
    public List<ExamDTO> findExpiredExams(String userId) {
        return List.of();
    }

    // ================= BUSINESS =================

    @Override
    public boolean checkParticipate(String userId, String examId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(exam.getOpenDate()) ||
                now.isAfter(exam.getCloseDate())) {
            return false;
        }

        return examRepository.checkParticipate(userId, examId);
    }

    @Override
    public void assignCourse(String examId, String courseId) {

        requireAdminOrTeacher();

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        exam.setCourse(courseRepository.getReferenceById(courseId));

        examRepository.save(exam);
    }

    @Override
    public List<ExamDTO> findAllByUserId(String userId) {
        return List.of();
    }

    @Override
    public List<ExamDTO> findAllByTeacherId(String teacherId) {
        return List.of();
    }

    // ================= HELPER =================

    private void applyRelations(Exam exam, ExamDTO dto) {

        if (dto.getCourseId() != null) {
            exam.setCourse(
                    courseRepository.getReferenceById(dto.getCourseId())
            );
        }
    }

    private void requireAdminOrTeacher() {
        // Spring Security later
    }
}