package com.exam.exam_web.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubjectDTO implements Serializable {
    private String subjectId;
    private String name;
    private String image;
    private String description;
}