package com.exam.exam_web.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubmissionDTO {

    // Thông tin lượt nộp bài
    private String examHistoryId;
    private Integer attemptNumber;
    private Double score;
    private Integer elapsedSeconds;
    private LocalDateTime submittedAt;

    private String userId;
    private String username;
    private String email;
}