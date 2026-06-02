package com.exam.exam_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"enrollments", "hibernateLazyInitializer", "handler"})

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String courseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Account teacher;

    private String courseName;

    private String description;

    private LocalDateTime startDate;

    private String academicYear;

    private String semester;

    private double progress;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private CourseStatus status;

    @OneToMany(mappedBy = "course")
    private List<Enrollment> enrollments;
}
