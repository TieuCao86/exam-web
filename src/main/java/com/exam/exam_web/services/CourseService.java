package com.exam.exam_web.services;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.PageResponse; // Import DTO cấu trúc phân trang mới tạo

import java.util.List;

public interface CourseService {

    CourseDTO create(CourseDTO dto);

    CourseDTO update(CourseDTO dto);

    boolean delete(String courseId);

    List<CourseDTO> findAll();

    CourseDTO findById(String courseId);

    List<CourseDTO> findByTeacher(String teacherId);

    List<CourseDTO> findByUser(String userId);

    CourseDTO getCourseByExamId(String examId);

    CourseDTO assignTeacher(String courseId, String teacherId);

    List<CourseDTO> findByFilter(String semester, String academicYear);

    List<CourseDTO> search(
            String keyword,
            String semester,
            String academicYear
    );

    PageResponse<CourseDTO> getAllCoursesForAdmin(
            String keyword,
            String semester,
            String academicYear,
            int page,
            int size,
            String sortBy
    );

    PageResponse<CourseDTO> getCoursesByTeacher(
            String teacherId,
            int page,
            int size
    );

    PageResponse<CourseDTO> getCoursesByStudent(
            String studentId,
            int page,
            int size
    );
}