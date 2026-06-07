package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.ExamHistorySummaryDTO;
import com.exam.exam_web.entity.ExamHistory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamHistorySummaryMapper {

    @Mapping(source = "exam.examId", target = "examId")
    @Mapping(source = "exam.examName", target = "examName")
    @Mapping(source = "exam.course.subject.name", target = "subjectName")
    @Mapping(source = "submittedAt", target = "lastSubmittedAt")
    ExamHistorySummaryDTO toDTO(ExamHistory entity);
}