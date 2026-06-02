package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.AnswerDTO;
import com.exam.exam_web.entity.Answer;
import com.exam.exam_web.mapper.AnswerMapper;
import com.exam.exam_web.repository.AnswerRepository;
import com.exam.exam_web.services.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnswerServiceImpl
        implements AnswerService {

    private final AnswerRepository answerRepository;
    private final AnswerMapper answerMapper;

    @Override
    public List<AnswerDTO> findByQuestion(
            String questionId
    ) {

        List<Answer> answers =
                answerRepository
                        .findByQuestion_QuestionId(
                                questionId
                        );

        return answers
                .stream()
                .map(answerMapper::toDTO)
                .toList();
    }
}