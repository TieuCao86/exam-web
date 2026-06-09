package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.AnswerDTO;
import com.exam.exam_web.entity.Answer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AnswerMapper {

    @Mapping(source = "question.questionId", target = "questionId")
    @Mapping(source = "correct", target = "isCorrect")
    AnswerDTO toDTO(Answer answer);

    @Mapping(source = "questionId", target = "question.questionId")
    Answer toEntity(AnswerDTO dto);
}