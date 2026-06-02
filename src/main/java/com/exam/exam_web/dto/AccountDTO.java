package com.exam.exam_web.dto;

import lombok.*;
import com.exam.exam_web.entity.Role;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDTO {
    private String accountId;
    private String username;
    private String email;

    private String passwordHash;
    private Role role;
    private boolean active;
}
