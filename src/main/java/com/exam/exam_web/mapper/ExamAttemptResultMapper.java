package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.ExamAttemptResultDTO;
import com.exam.exam_web.entity.ExamHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamAttemptResultMapper {

    @Mapping(source = "exam.examName", target = "examName")
    @Mapping(source = "exam.questionAmount", target = "totalQuestions")
    ExamAttemptResultDTO toDTO(ExamHistory entity);
}