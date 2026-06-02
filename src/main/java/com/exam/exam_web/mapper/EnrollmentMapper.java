package com.exam.exam_web.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.exam.exam_web.dto.EnrollmentDTO;
import com.exam.exam_web.entity.Enrollment;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(source = "user.userId", target = "userId")
    @Mapping(source = "course.courseId", target = "courseId")
    EnrollmentDTO toDTO(Enrollment enrollment);

    @Mapping(source = "userId", target = "user.userId")
    @Mapping(source = "courseId", target = "course.courseId")
    Enrollment toEntity(EnrollmentDTO dto);
}