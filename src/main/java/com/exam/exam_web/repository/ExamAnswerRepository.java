package com.exam.exam_web.repository;

import com.exam.exam_web.entity.ExamAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamAnswerRepository extends JpaRepository<ExamAnswer, String> {

    List<ExamAnswer> findByExamHistory_ExamHistoryId(String examHistoryId);
}