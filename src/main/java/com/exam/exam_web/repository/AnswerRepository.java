package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository
        extends JpaRepository<Answer, String> {

    List<Answer> findByQuestion_QuestionId(
            String questionId
    );
}