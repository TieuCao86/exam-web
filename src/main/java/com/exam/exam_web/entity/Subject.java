package com.exam.exam_web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"courses", "teachers", "hibernateLazyInitializer", "handler"})

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String subjectId;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private String image;
}