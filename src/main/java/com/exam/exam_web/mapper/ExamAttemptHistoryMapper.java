package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.ExamAttemptHistoryDTO;
import com.exam.exam_web.entity.ExamHistory;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ExamAttemptHistoryMapper {

    ExamAttemptHistoryDTO toDTO(ExamHistory entity);

    ExamHistory toEntity(ExamAttemptHistoryDTO dto);
}