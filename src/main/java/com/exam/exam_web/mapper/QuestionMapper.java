package com.exam.exam_web.mapper;

import com.exam.exam_web.dto.QuestionDTO;
import com.exam.exam_web.entity.Exam;
import com.exam.exam_web.entity.Question;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AnswerMapper.class})
public interface QuestionMapper {

    @Mapping(source = "subject.subjectId", target = "subjectId")
    QuestionDTO toDTO(Question question);

    @Mapping(target = "subject", ignore = true)
    Question toEntity(QuestionDTO dto);
}