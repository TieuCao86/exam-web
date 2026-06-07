package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.ExamAnsweredDTO;
import com.exam.exam_web.entity.ExamAnswer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamAnsweredMapper {

    // Map từ Entity sang DTO
    @Mapping(source = "examHistory.examHistoryId", target = "examHistoryId")
    @Mapping(target = "selectedAnswerId", ignore = true)
    ExamAnsweredDTO toDTO(ExamAnswer entity);

    // Map ngược từ DTO sang Entity
    @Mapping(target = "examHistory", ignore = true)
    ExamAnswer toEntity(ExamAnsweredDTO dto);
}