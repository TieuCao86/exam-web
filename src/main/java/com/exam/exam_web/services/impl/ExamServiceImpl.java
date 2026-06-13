package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.dto.PageResponse;
import com.exam.exam_web.entity.Exam;
import com.exam.exam_web.mapper.ExamMapper;
import com.exam.exam_web.repository.CourseRepository;
import com.exam.exam_web.repository.ExamRepository;
import com.exam.exam_web.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;          // 💡 Thêm import xử lý Page
import org.springframework.data.domain.PageRequest;   // 💡 Thêm import tạo Request trang
import org.springframework.data.domain.Pageable;      // 💡 Thêm import tham số phân trang
import org.springframework.data.domain.Sort;          // 💡 Thêm import sắp xếp dữ liệu
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;                     // 💡 Thêm import Collectors

@RequiredArgsConstructor
@Service
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final ExamMapper examMapper;

    // ================= CRUD =================

    @Override
    @Transactional
    public ExamDTO createExam(ExamDTO dto) {

        requireAdminOrTeacher();

        Exam exam = examMapper.toEntity(dto);

        exam.setCreatedAt(LocalDateTime.now());
        exam.setUpdatedAt(LocalDateTime.now());

        applyRelations(exam, dto);

        return examMapper.toDTO(examRepository.save(exam));
    }

    @Override
    @Transactional
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
    @Transactional
    public boolean deleteExam(String examId) {

        requireAdminOrTeacher();

        if (!examRepository.existsById(examId)) return false;

        examRepository.deleteById(examId);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public ExamDTO findById(String examId) {
        return examRepository.findById(examId)
                .map(examMapper::toDTO)
                .orElse(null);
    }

    // ================= QUERIES =================

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findAll() {
        return examRepository.findAll()
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findByName(String name) {
        return examRepository.findByExamNameContainingIgnoreCase(name)
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findByCourse(String courseId) {
        return examRepository.findByCourse_CourseId(courseId)
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    // ================= STATUS =================

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findAvailableExams() {
        LocalDateTime now = LocalDateTime.now();

        return examRepository
                .findByOpenDateBeforeAndCloseDateAfter(now, now)
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findUpcomingExams() {
        return examRepository.findByOpenDateAfter(LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findExpiredExams() {
        return examRepository.findByCloseDateBefore(LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    // ================= BUSINESS =================

    @Override
    @Transactional(readOnly = true)
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
    @Transactional
    public void assignCourse(String examId, String courseId) {

        requireAdminOrTeacher();

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        exam.setCourse(courseRepository.getReferenceById(courseId));

        examRepository.save(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findAllByUserId(String userId) {
        return examRepository.findAllByUserId(userId).stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findAllByTeacherId(String teacherId) {
        return List.of();
    }

    // ================= PASSWORD VERIFICATION =================

    @Override
    @Transactional(readOnly = true)
    public boolean verifyPassword(String examId, String password) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        if (exam.getPassword() == null || exam.getPassword().trim().isEmpty()) {
            return true;
        }

        return exam.getPassword().equals(password);
    }

    // =====================================================================
    // 🚀 ĐÃ HOÀN THIỆN: LOGIC PHÂN TRANG (ÉP CỨNG ĐÚNG 12 ĐỀ THI / TRANG)
    // =====================================================================

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamDTO> findAllPaged(String keyword, int page) {
        // Tạo Pageable ép cứng size = 12 phần tử, sắp xếp theo ngày mở đề mới nhất
        Pageable pageable = PageRequest.of(page, 12, Sort.by("openDate").descending());
        Page<Exam> examPage = examRepository.searchExams(keyword, pageable);

        List<ExamDTO> dtoList = examPage.getContent().stream()
                .map(examMapper::toDTO)
                .collect(Collectors.toList());

        return PageResponse.<ExamDTO>builder()
                .content(dtoList)
                .pageNumber(examPage.getNumber())
                .pageSize(examPage.getSize())
                .totalElements(examPage.getTotalElements())
                .totalPages(examPage.getTotalPages())
                .isLast(examPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamDTO> findAvailableExamsPaged(String userId, int page) {
        // Sinh viên xem đề thi đang mở làm bài, sắp xếp tăng dần theo hạn đóng đề gần nhất
        Pageable pageable = PageRequest.of(page, 12, Sort.by("openDate").ascending());
        Page<Exam> examPage = examRepository.findAvailableExamsPaged(userId, LocalDateTime.now(), pageable);

        List<ExamDTO> dtoList = examPage.getContent().stream().map(examMapper::toDTO).toList();

        return PageResponse.<ExamDTO>builder()
                .content(dtoList)
                .pageNumber(examPage.getNumber())
                .pageSize(examPage.getSize())
                .totalElements(examPage.getTotalElements())
                .totalPages(examPage.getTotalPages())
                .isLast(examPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamDTO> findUpcomingExamsPaged(String userId, int page) {
        // Sinh viên xem lịch thi sắp tới, sắp xếp tăng dần theo thời gian chuẩn bị mở
        Pageable pageable = PageRequest.of(page, 12, Sort.by("openDate").ascending());
        Page<Exam> examPage = examRepository.findUpcomingExamsPaged(userId, LocalDateTime.now(), pageable);

        List<ExamDTO> dtoList = examPage.getContent().stream().map(examMapper::toDTO).toList();

        return PageResponse.<ExamDTO>builder()
                .content(dtoList)
                .pageNumber(examPage.getNumber())
                .pageSize(examPage.getSize())
                .totalElements(examPage.getTotalElements())
                .totalPages(examPage.getTotalPages())
                .isLast(examPage.isLast())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ExamDTO> findExpiredExamsPaged(String userId, int page) {
        // Sinh viên xem lại lịch sử các đề đã đóng cổng, xếp giảm dần theo hạn đóng muộn nhất
        Pageable pageable = PageRequest.of(page, 12, Sort.by("closeDate").descending());
        Page<Exam> examPage = examRepository.findExpiredExamsPaged(userId, LocalDateTime.now(), pageable);

        List<ExamDTO> dtoList = examPage.getContent().stream().map(examMapper::toDTO).toList();

        return PageResponse.<ExamDTO>builder()
                .content(dtoList)
                .pageNumber(examPage.getNumber())
                .pageSize(examPage.getSize())
                .totalElements(examPage.getTotalElements())
                .totalPages(examPage.getTotalPages())
                .isLast(examPage.isLast())
                .build();
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
    @Transactional(readOnly = true)
    public List<ExamDTO> findAvailableExams(String userId) {
        return examRepository.findAvailableExams(userId, LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findUpcomingExams(String userId) {
        return examRepository.findUpcomingExams(userId, LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamDTO> findExpiredExams(String userId) {
        return examRepository.findExpiredExams(userId, LocalDateTime.now())
                .stream()
                .map(examMapper::toDTO)
                .toList();
    }
}