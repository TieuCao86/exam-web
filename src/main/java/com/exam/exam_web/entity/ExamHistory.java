package com.exam.exam_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "exam_histories")
public class ExamHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String examHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int attemptNumber;

    private int elapsedSeconds;

    private Double score;

    private String snapshotId;

    private LocalDateTime submittedAt;

    @CreationTimestamp // Tự động ghi nhận mốc thời gian hệ thống khi sinh viên bấm Start bài thi
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "cheat_count", nullable = false)
    private int cheatCount = 0;
}