package com.exam.exam_web.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String userId;
    private String fullName;
    private String avatar;
    private String className;

    private AccountDTO account;
}