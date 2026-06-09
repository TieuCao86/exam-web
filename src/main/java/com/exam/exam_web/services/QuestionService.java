package com.exam.exam_web.services;

import com.exam.exam_web.dto.ExamQuestionDTO;
import com.exam.exam_web.dto.QuestionDTO;

import java.util.List;

public interface QuestionService {

    // CRUD

    QuestionDTO createQuestion(
            QuestionDTO dto
    );

    QuestionDTO updateQuestion(
            QuestionDTO dto
    );

    boolean deleteQuestion(
            String questionId
    );

    QuestionDTO findById(
            String questionId
    );

    List<QuestionDTO> findAll();

    // Queries

    List<QuestionDTO> findByContent(
            String keyword
    );

    List<QuestionDTO> findByExam(
            String examId
    );

    List<QuestionDTO> findBySubject(
            String subjectId
    );

    // ===== EXAM TAKING =====

    List<ExamQuestionDTO> getExamQuestions(
            String examId
    );
}