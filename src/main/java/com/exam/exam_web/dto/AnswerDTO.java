package com.exam.exam_web.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDTO implements Serializable {
    private String answerId;
    private String content;
    private boolean isCorrect;
    private String questionId;
}