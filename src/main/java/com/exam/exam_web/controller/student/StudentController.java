package com.exam.exam_web.controller.student;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.dto.OptionDTO;
import com.exam.exam_web.services.CourseService;
import com.exam.exam_web.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Controller
public class StudentController {

    private final CourseService courseService;
    private final ExamService examService;

    @GetMapping("/calendar")
    public String showCalendar() {
        return "student/calendar";
    }

    @GetMapping("/courses")
    public String getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String semester,

            @RequestParam(required = false) String year,
            Model model
    ) {

        keyword = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        semester = (semester == null || semester.isBlank()) ? null : semester.trim();
        year = (year == null || year.isBlank()) ? null : year.trim();

        List<CourseDTO> courses =
                courseService.search(
                        keyword,
                        semester,
                        year
                );

        model.addAttribute("courses", courses);

        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("selectedYear", year);

        model.addAttribute("semesterOptions", List.of(
                new OptionDTO("HK1", "Học kỳ 1"),
                new OptionDTO("HK2", "Học kỳ 2")
        ));

        model.addAttribute("yearOptions", List.of(
                new OptionDTO("2024-2025", "2024-2025"),
                new OptionDTO("2025-2026", "2025-2026")
        ));

        model.addAttribute("currentPage", 0);
        model.addAttribute("hasNext", false);

        return "student/courses";
    }

    @GetMapping("/exams")
    public String getExams(Model model) {

        model.addAttribute("exams", examService.findAll());

        return "student/exams";
    }

    @GetMapping("/courses/{id}")
    public String courseDetail(
            @PathVariable String id,
            Model model) {

        CourseDTO course = courseService.findById(id);

        if (course == null) {
            return "redirect:/courses";
        }

        model.addAttribute("course", course);

        return "student/course-detail";
    }
}