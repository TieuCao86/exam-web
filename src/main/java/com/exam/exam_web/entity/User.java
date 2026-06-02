package com.exam.exam_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor

@Entity
@Table(name = "users")
@JsonIgnoreProperties({
        "subjects",
        "hibernateLazyInitializer",
        "handler"
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false, unique = true)
    private Account account;

    private String fullName;

    private LocalDate dateOfBirth;

    private String avatar;

    private String className;

    @OneToMany(mappedBy = "user")
    private List<Enrollment> enrollments;
}

