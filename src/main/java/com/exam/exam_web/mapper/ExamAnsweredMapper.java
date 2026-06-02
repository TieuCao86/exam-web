package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.ExamAnsweredDTO;
import com.exam.exam_web.entity.Answer;
import com.exam.exam_web.entity.ExamAnswer;
import com.exam.exam_web.entity.ExamHistory;
import com.exam.exam_web.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ExamAnsweredMapper {

    @Mapping(source = "examHistory.examHistoryId", target = "examHistoryId")
    @Mapping(source = "question.questionId", target = "questionId")
    @Mapping(source = "selectedAnswer.answerId", target = "selectedAnswerId")
    ExamAnsweredDTO toDTO(ExamAnswer entity);

    @Mapping(source = "examHistoryId", target = "examHistory.examHistoryId")
    @Mapping(source = "questionId", target = "question.questionId")
    @Mapping(source = "selectedAnswerId", target = "selectedAnswer.answerId")
    ExamAnswer toEntity(ExamAnsweredDTO dto);
}