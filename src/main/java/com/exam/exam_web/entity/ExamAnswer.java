package com.exam.exam_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "exam_answers")
public class ExamAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String examAnswerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_history_id", nullable = false)
    private ExamHistory examHistory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_answer_id")
    private Answer selectedAnswer;

    private boolean correct;

    private double grade;
}