package com.exam.exam_web.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    private String url;
    private String icon;
    private String text;
}
