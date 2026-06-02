package com.exam.exam_web.controller.student;

import com.exam.exam_web.entity.Course;
import com.exam.exam_web.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CalendarController {

    private final CourseRepository courseRepository;

    // constructor injection
    public CalendarController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping("/calendar")
    public String showCalendar() {
        return "student/calendar";
    }

    @GetMapping("/courses")
    public String getCourses(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        List<Course> fakeCourses = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            Course c = new Course();
            c.setCourseName("Khóa học " + i);
            c.setAcademicYear("2025-2026");
            c.setImageUrl("/images/background_course.jpg");
            c.setSemester("HK1");
            c.setProgress((i * 10) % 100);
            fakeCourses.add(c);
        }

        model.addAttribute("courses", fakeCourses);
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", true);

        return "student/courses";
    }
}