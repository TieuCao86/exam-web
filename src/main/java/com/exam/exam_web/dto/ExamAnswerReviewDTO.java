package com.exam.exam_web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnswerReviewDTO {

    private Integer orderInExam;

    private String questionContent;

    private String selectedAnswer;

    private String correctAnswer;

    private boolean correct;

    private Double grade;
}
