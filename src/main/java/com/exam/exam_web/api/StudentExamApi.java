package com.exam.exam_web.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class StudentExamApi {
    @GetMapping("/api/student/exams")
    public List<Map<String, Object>> getEvents() {

        return List.of(

                Map.of(
                        "id", "EX001",
                        "title", "Java Exam",
                        "start", "2026-05-28"
                ),

                Map.of(
                        "id", "EX002",
                        "title", "Math Exam",
                        "start", "2026-05-30"
                )
        );
    }
}
