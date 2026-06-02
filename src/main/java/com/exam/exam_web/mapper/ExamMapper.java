package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.entity.Exam;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ExamMapper {

    @Mapping(source = "course.courseId", target = "courseId")
    @Mapping(source = "course.courseName", target = "courseName")

    // SUBJECT
    @Mapping(source = "course.subject.subjectId", target = "subjectId")
    @Mapping(source = "course.subject.name", target = "subjectName")

    // TEACHER = ACCOUNT (FIX)
    @Mapping(source = "course.teacher.accountId", target = "teacherId")
    @Mapping(source = "course.teacher.username", target = "teacherName")

    ExamDTO toDTO(Exam exam);

    @Mapping(target = "course", ignore = true)
    @Mapping(target = "examId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Exam toEntity(ExamDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "examId", ignore = true)
    @Mapping(target = "course", ignore = true)
    void updateEntity(ExamDTO dto, @MappingTarget Exam entity);
}