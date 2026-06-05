package com.exam.exam_web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonDTO {
    private String lessonId;
    private String chapterId;
    private String title;
    private String content;
    private String lessonType;
    private int orderIndex;
}