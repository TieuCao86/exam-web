package com.exam.exam_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"chapter", "hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "lessons")
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String lessonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;

    @Column(nullable = false)
    private String title; // Ví dụ: "1.1 Tổng quan"

    @Column(columnDefinition = "TEXT")
    private String content; // Nội dung văn bản hoặc link video/tài liệu bài học

    private String lessonType; // Kiểu bài học: "VIDEO", "TEXT", "FILE", v.v.

    @Column(name = "order_index", nullable = false)
    private int orderIndex; // Thứ tự sắp xếp bài học trong chương
}