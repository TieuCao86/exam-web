package com.exam.exam_web.services;

import com.exam.exam_web.dto.ExamDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ExamService {

    // CRUD

    ExamDTO createExam(
            ExamDTO examDTO
    );

    ExamDTO updateExam(
            ExamDTO examDTO
    );

    boolean deleteExam(
            String examId
    );

    ExamDTO findById(
            String examId
    );

    List<ExamDTO> findAll();

    // Queries

    List<ExamDTO> findByName(
            String examName
    );

    List<ExamDTO> findByCourse(
            String courseId
    );

    // Status filters

    List<ExamDTO> findAvailableExams();

    List<ExamDTO> findUpcomingExams();

    List<ExamDTO> findExpiredExams();

    List<ExamDTO> findAvailableExams(
            String userId
    );

    List<ExamDTO> findUpcomingExams(
            String userId
    );

    List<ExamDTO> findExpiredExams(
            String userId
    );

    boolean checkParticipate(
            String userId,
            String examId
    );

    // Assignment

    void assignCourse(
            String examId,
            String courseId
    );

    List<ExamDTO> findAllByUserId(
            String userId
    );

    List<ExamDTO> findAllByTeacherId(
            String teacherId
    );
}