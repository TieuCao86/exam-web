package com.exam.exam_web.api;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "http://localhost:5173")
public class CourseApiController {

    private final CourseService courseService;

    @GetMapping
    public List<CourseDTO> getAll() {
        return courseService.findAll();
    }
}