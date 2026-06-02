package com.exam.exam_web.dto;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CourseDTO implements Serializable {
    private String courseId;
    private String courseName;
    private String description;
    private LocalDateTime startDate;
    private String academicYear;
    private String semester;
    private double progress;
    private String imageUrl;

    private String subjectId;
    private String teacherId;
    private String teacherName;
    private String status;
}