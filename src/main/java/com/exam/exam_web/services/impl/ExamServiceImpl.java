package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.entity.Exam;
import com.exam.exam_web.mapper.ExamMapper;
import com.exam.exam_web.repository.CourseRepository;
import com.exam.exam_web.repository.ExamRepository;
import com.exam.exam_web.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final ExamMapper examMapper;

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

    // ================= PASSWORD VERIFICATION =================

    @Override
    public boolean verifyPassword(String examId, String password) {
        // 1. Tìm thông tin đề thi trong Database
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        // 2. Nếu cấu hình đề thi KHÔNG CÓ mật khẩu (null hoặc rỗng) -> Coi như hợp lệ luôn
        if (exam.getPassword() == null || exam.getPassword().trim().isEmpty()) {
            return true;
        }

        // 3. Nếu đề có mật khẩu, tiến hành so sánh chính xác chuỗi ký tự nhận được
        // (Nếu sau này bạn dùng BCrypt mã hóa mật khẩu đề thi, hãy đổi sang passwordEncoder.matches tại đây)
        return exam.getPassword().equals(password);
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

    @Override
    public List<ExamDTO> findAvailableExams(String userId) {
        return examRepository.findAvailableExams(userId, LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamDTO> findUpcomingExams(String userId) {
        return examRepository.findUpcomingExams(userId, LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    public List<ExamDTO> findExpiredExams(String userId) {
        return examRepository.findExpiredExams(userId, LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }
}