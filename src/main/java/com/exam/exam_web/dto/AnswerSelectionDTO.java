package com.exam.exam_web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSelectionDTO {
    private String questionId;
    private String selectedAnswerId; // Nếu câu đó học sinh bỏ trống không khoanh thì Frontend gửi null hoặc chuỗi rỗng ""
}