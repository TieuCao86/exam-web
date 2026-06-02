package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.SubjectDTO;
import com.exam.exam_web.entity.Subject;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SubjectMapper {

    SubjectDTO toDTO(Subject subject);

    Subject toEntity(SubjectDTO dto);
}