package com.exam.exam_web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {
    private String value;
    private String label;
}
