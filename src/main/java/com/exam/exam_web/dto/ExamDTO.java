package com.exam.exam_web.dto;

import com.exam.exam_web.entity.ExamType;
import com.exam.exam_web.entity.GradingMethod;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO implements Serializable {

    private String examId;
    private String examName;
    private String password;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int questionAmount;

    private LocalDateTime openDate;
    private LocalDateTime closeDate;
    private int durationMinutes;

    private String description;

    private ExamType examType;

    private Integer maxAttempts;

    private GradingMethod gradingMethod;

    private String courseId;
    private String courseName;

    private String subjectId;
    private String subjectName;

    private String teacherId;
    private String teacherName;
}