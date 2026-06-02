package com.exam.exam_web.services;

import com.exam.exam_web.dto.ExamAnsweredDTO;

import java.util.List;

public interface ExamAnsweredService {

    List<ExamAnsweredDTO> findByExamHistory(String examHistoryId);

    ExamAnsweredDTO findByExamHistoryAndQuestion(String examHistoryId, String questionId);
}