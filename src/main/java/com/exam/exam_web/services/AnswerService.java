package com.exam.exam_web.services;

import com.exam.exam_web.dto.AnswerDTO;

import java.util.List;

public interface AnswerService {

    List<AnswerDTO> findByQuestion(
            String questionId
    );
}