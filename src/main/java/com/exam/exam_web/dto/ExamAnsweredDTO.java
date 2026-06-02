package com.exam.exam_web.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ExamAnsweredDTO implements Serializable {
    private String examAnswerId;
    private String examHistoryId;
    private String questionId;
    private String selectedAnswerId;
    private int orderInExam;
    private double grade;
}