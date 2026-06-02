package com.exam.exam_web.services;

import com.exam.exam_web.dto.CourseDTO;

import java.util.List;

public interface CourseService {

    CourseDTO create(
            CourseDTO dto
    );

    CourseDTO update(
            CourseDTO dto
    );

    boolean delete(
            String courseId
    );

    List<CourseDTO> findAll();

    CourseDTO findById(
            String courseId
    );

    List<CourseDTO> findByTeacher(
            String teacherId
    );

    List<CourseDTO> findByUser(
            String userId
    );

    CourseDTO getCourseByExamId(
            String examId
    );

    CourseDTO assignTeacher(
            String courseId,
            String teacherId
    );
}