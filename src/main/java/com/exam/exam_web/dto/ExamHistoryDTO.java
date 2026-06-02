package com.exam.exam_web.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamHistoryDTO implements Serializable {
    private String examHistoryId;
    private String examId;
    private String userId;
    private int attemptNumber;
    private LocalDateTime submittedAt;
    private String examName;
    private String subjectName;
    private int elapsedSeconds;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
}