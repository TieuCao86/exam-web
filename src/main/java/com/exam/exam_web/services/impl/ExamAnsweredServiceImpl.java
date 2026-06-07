package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.ExamAnsweredDTO;
import com.exam.exam_web.entity.ExamAnswer;
import com.exam.exam_web.mapper.ExamAnsweredMapper;
import com.exam.exam_web.repository.ExamAnsweredRepository;
import com.exam.exam_web.services.ExamAnsweredService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExamAnsweredServiceImpl implements ExamAnsweredService {

    private final ExamAnsweredRepository examAnsweredRepository;
    private final ExamAnsweredMapper examAnsweredMapper;

    public ExamAnsweredServiceImpl(
            ExamAnsweredRepository examAnsweredRepository,
            ExamAnsweredMapper examAnsweredMapper
    ) {
        this.examAnsweredRepository = examAnsweredRepository;
        this.examAnsweredMapper = examAnsweredMapper;
    }

    @Override
    public List<ExamAnsweredDTO> findByExamHistory(String examHistoryId) {

        if (examHistoryId == null) {
            throw new IllegalArgumentException("examHistoryId không được null");
        }

        return examAnsweredRepository
                .findByExamHistory_ExamHistoryId(examHistoryId)
                .stream()
                .map(examAnsweredMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamAnsweredDTO findByExamHistoryAndQuestion(String examHistoryId, String questionId) {

        if (examHistoryId == null || questionId == null) {
            throw new IllegalArgumentException("examHistoryId hoặc questionId null");
        }

        return examAnsweredRepository
                .findByExamHistory_ExamHistoryIdAndQuestionId(
                        examHistoryId,
                        questionId
                )
                .map(examAnsweredMapper::toDTO)
                .orElse(null);
    }
}