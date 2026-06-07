package com.exam.exam_web.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "exam_snapshots")
public class ExamSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String snapshotId;

    private String examId;
    private String userId;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String snapshotData;
}