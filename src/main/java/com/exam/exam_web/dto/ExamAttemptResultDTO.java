package com.exam.exam_web.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttemptResultDTO {

    private String examHistoryId;

    private String examName;

    private Integer attemptNumber;

    private Double score;

    private Integer elapsedSeconds;

    private LocalDateTime submittedAt;

    private Integer totalQuestions;

    private Integer correctAnswers;

    private List<ExamAnswerReviewDTO> answers;
}