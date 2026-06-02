package com.exam.exam_web.dto;

import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QuestionDTO {
    private String questionId;
    private String content;
    private String subjectId;
    private List<String> examIds;

    private List<AnswerDTO> answers;
}