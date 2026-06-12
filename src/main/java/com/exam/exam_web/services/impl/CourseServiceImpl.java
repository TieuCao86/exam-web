package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.entity.Course;
import com.exam.exam_web.entity.CourseStatus;
import com.exam.exam_web.entity.Subject;
import com.exam.exam_web.entity.Account; // 💡 Đổi từ User sang Account
import com.exam.exam_web.repository.CourseRepository;
import com.exam.exam_web.repository.SubjectRepository;
import com.exam.exam_web.repository.AccountRepository; // 💡 Inject AccountRepository thay vì UserRepository
import com.exam.exam_web.services.CourseService;
import com.exam.exam_web.mapper.CourseMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final SubjectRepository subjectRepository;
    private final AccountRepository accountRepository; // 💡 Đã chuyển đổi sang AccountRepository
    private final CourseMapper courseMapper;

    @Override
    @Transactional
    public CourseDTO create(CourseDTO dto) {
        if (dto.getCourseId() != null && courseRepository.existsById(dto.getCourseId())) {
            throw new RuntimeException("Mã khóa học '" + dto.getCourseId() + "' đã tồn tại!");
        }

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy môn học: " + dto.getSubjectId()));

        // 💡 Tìm trực tiếp tài khoản giáo viên trong bảng accounts (khớp với 'acc-teacher-001' trong SQL)
        Account teacherAccount = accountRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Giảng viên có mã tài khoản: " + dto.getTeacherId()));

        CourseStatus status = CourseStatus.OPEN;
        if (dto.getStatus() != null) {
            try {
                status = CourseStatus.valueOf(dto.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                status = CourseStatus.OPEN;
            }
        }

        Course course = Course.builder()
                .courseId(dto.getCourseId())
                .courseName(dto.getCourseName())
                .description(dto.getDescription())
                .startDate(dto.getStartDate())
                .academicYear(dto.getAcademicYear())
                .semester(dto.getSemester())
                .progress(dto.getProgress()) // Đã tối ưu theo kiểu double nguyên thủy
                .imageUrl(dto.getImageUrl())
                .status(status)
                .subject(subject)
                .teacher(teacherAccount) // 💡 Gán trực tiếp thực thể Account vào liên kết
                .build();

        Course savedCourse = courseRepository.save(course);
        return courseMapper.toDTO(savedCourse);
    }

    @Override
    @Transactional
    public CourseDTO update(CourseDTO dto) {
        Course existingCourse = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học cần cập nhật!"));

        existingCourse.setCourseName(dto.getCourseName());
        existingCourse.setDescription(dto.getDescription());
        existingCourse.setAcademicYear(dto.getAcademicYear());
        existingCourse.setSemester(dto.getSemester());

        if (dto.getStatus() != null) {
            existingCourse.setStatus(CourseStatus.valueOf(dto.getStatus().toUpperCase()));
        }
        if (dto.getProgress() != 0) { // Check theo giá trị mặc định của kiểu số nguyên thủy
            existingCourse.setProgress(dto.getProgress());
        }

        return courseMapper.toDTO(courseRepository.save(existingCourse));
    }

    @Override
    @Transactional
    public boolean delete(String courseId) {
        if (courseRepository.existsById(courseId)) {
            courseRepository.deleteById(courseId);
            return true;
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findAll() {
        return courseRepository.findAll().stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO findById(String courseId) {
        return courseRepository.findById(courseId)
                .map(courseMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Course not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findByTeacher(String teacherId) {
        return courseRepository.findByTeacher_AccountId(teacherId).stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findByUser(String userId) {
        return courseRepository.findByEnrollments_User_Account_AccountId(userId).stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseDTO getCourseByExamId(String examId) {
        return courseRepository.findByExamId(examId)
                .map(courseMapper::toDTO)
                .orElse(null);
    }

    @Override
    @Transactional
    public CourseDTO assignTeacher(String courseId, String teacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Account teacherAccount = accountRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher account not found"));

        course.setTeacher(teacherAccount);
        return courseMapper.toDTO(courseRepository.save(course));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> findByFilter(String semester, String academicYear) {
        return courseRepository.findBySemesterAndAcademicYear(semester, academicYear).stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseDTO> search(String keyword, String semester, String academicYear) {
        return courseRepository.searchCourses(keyword, semester, academicYear).stream()
                .map(courseMapper::toDTO)
                .collect(Collectors.toList());
    }
}