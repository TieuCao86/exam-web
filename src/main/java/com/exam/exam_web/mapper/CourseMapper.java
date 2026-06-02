package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.entity.Course;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CourseMapper {

    CourseDTO toDTO(Course course);

    Course toEntity(CourseDTO dto);
}