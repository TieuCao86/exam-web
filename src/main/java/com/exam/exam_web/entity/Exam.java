package com.exam.exam_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "questions"})
@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String examId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private String examName;

    private String password;

    private String description;

    @Enumerated(EnumType.STRING)
    private ExamType examType;

    private int durationMinutes;

    private int questionAmount;

    private Integer maxAttempts;

    @Enumerated(EnumType.STRING)
    private GradingMethod gradingMethod;

    private LocalDateTime openDate;
    private LocalDateTime closeDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}