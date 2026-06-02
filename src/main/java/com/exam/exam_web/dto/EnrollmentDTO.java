package com.exam.exam_web.dto;

import lombok.*;
import com.exam.exam_web.entity.EnrollmentStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO implements Serializable {
    private String enrollmentId;
    private String userId;
    private String courseId;
    private LocalDateTime enrolledAt;
    private EnrollmentStatus status;
}