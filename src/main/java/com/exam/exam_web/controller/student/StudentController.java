package com.exam.exam_web.controller.student;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.dto.OptionDTO;
import com.exam.exam_web.entity.Course;
import com.exam.exam_web.repository.CourseRepository;
import com.exam.exam_web.services.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StudentController {

    private final CourseService courseService;

    public StudentController(CourseService courseService) {
        this.courseService = courseService;
    }

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

        List<ExamDTO> fakeExams = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {

            ExamDTO e = new ExamDTO();

            e.setExamId("EX" + i);
            e.setExamName("Kỳ thi giữa kỳ " + i);

            e.setPassword(i % 2 == 0 ? "123456" : null);

            e.setCreatedAt(LocalDateTime.now().minusDays(i));
            e.setUpdatedAt(LocalDateTime.now());

            e.setQuestionAmount(20 + i);

            e.setOpenDate(LocalDateTime.now().minusDays(1));
            e.setCloseDate(LocalDateTime.now().plusDays(2));

            e.setDurationMinutes(60);

            e.setCourseId("C" + i);
            e.setCourseName("Khóa học " + i);

            e.setSubjectId("S" + i);
            e.setSubjectName("Lập trình Java " + i);

            e.setTeacherId("T" + i);
            e.setTeacherName("Giảng viên " + i);

            // ================= STATUS LOGIC TEST =================
            if (i % 3 == 0) {
                e.setCloseDate(LocalDateTime.now().minusDays(1)); // đã đóng
            } else if (i % 2 == 0) {
                e.setOpenDate(LocalDateTime.now().minusHours(2)); // đang mở
                e.setCloseDate(LocalDateTime.now().plusDays(1));
            } else {
                e.setOpenDate(LocalDateTime.now().plusDays(1)); // chưa mở
            }

            fakeExams.add(e);
        }

        model.addAttribute("exams", fakeExams);

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