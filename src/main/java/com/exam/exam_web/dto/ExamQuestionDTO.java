package com.exam.exam_web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionDTO {

    private String questionId;

    private Integer orderInExam;

    private String content;

    private List<ExamOptionDTO> answers;
}