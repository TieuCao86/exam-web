package com.exam.exam_web.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChapterDTO {
    private String chapterId;
    private String courseId;
    private String title;
    private int orderIndex;
    private List<LessonDTO> lessons; // Dùng để đổ cấu trúc cây menu con (1.1, 1.2...)
}