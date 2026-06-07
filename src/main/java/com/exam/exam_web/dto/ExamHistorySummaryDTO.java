package com.exam.exam_web.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamHistorySummaryDTO {

    private String examId;

    private String examName;

    private String subjectName;

    private Long totalAttempts;

    private Double bestScore;

    private LocalDateTime lastSubmittedAt;
}
