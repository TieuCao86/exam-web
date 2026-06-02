package com.exam.exam_web.services;

import com.exam.exam_web.dto.SubjectDTO;

import java.util.List;

public interface SubjectService {

    SubjectDTO createSubject(
            SubjectDTO dto
    );

    SubjectDTO findById(String id);

    List<SubjectDTO> findAll();

    SubjectDTO updateSubject(
            String id,
            SubjectDTO dto
    );

    void deleteSubject(String id);

    String getSubjectImageByCourseId(
            String courseId
    );
}