package com.exam.exam_web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAttemptHistoryDTO {

    private String examHistoryId;

    private Integer attemptNumber;

    private Double score;

    private Integer elapsedSeconds;

    private LocalDateTime submittedAt;
}