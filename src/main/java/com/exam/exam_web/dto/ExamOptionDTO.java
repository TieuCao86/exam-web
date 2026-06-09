package com.exam.exam_web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamOptionDTO {

    private String answerId;

    private String content;
}
