package com.exam.exam_web.repository;

import com.exam.exam_web.entity.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExamAnsweredRepository extends JpaRepository<ExamAnswer, String> {

    List<ExamAnswer> findByExamHistory_ExamHistoryId(String examHistoryId);

    Optional<ExamAnswer> findByExamHistory_ExamHistoryIdAndQuestion_QuestionId(
            String examHistoryId,
            String questionId
    );
}