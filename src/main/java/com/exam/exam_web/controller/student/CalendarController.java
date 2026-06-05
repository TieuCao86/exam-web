package com.exam.exam_web.controller.student;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.dto.OptionDTO;
import com.exam.exam_web.entity.Course;
import com.exam.exam_web.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@CrossOrigin(origins = "http://localhost:5173")
@RestController
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
            c.setCourseId(String.valueOf(i));
            c.setCourseName("Khóa học " + i);
            c.setAcademicYear(i % 2 == 0 ? "2025-2026" : "2024-2025");
            c.setImageUrl("/images/background_course.jpg");
            c.setSemester(i % 2 == 0 ? "HK1" : "HK2");
            c.setProgress((i * 10) % 100);
            fakeCourses.add(c);
        }

        // =========================
        // FILTER OPTIONS (TEST DATA)
        // =========================

        List<OptionDTO> semesterOptions = List.of(
                new OptionDTO("HK1", "Học kỳ 1"),
                new OptionDTO("HK2", "Học kỳ 2")
        );

        List<OptionDTO> yearOptions = List.of(
                new OptionDTO("2024-2025", "2024-2025"),
                new OptionDTO("2025-2026", "2025-2026")
        );

        // =========================
        // MODEL
        // =========================
        model.addAttribute("courses", fakeCourses);
        model.addAttribute("currentPage", page);
        model.addAttribute("hasNext", true);

        model.addAttribute("semesterOptions", semesterOptions);
        model.addAttribute("yearOptions", yearOptions);

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

        CourseDTO course = new CourseDTO();

        course.setCourseId(id);
        course.setCourseName("Khóa học " + id);

        model.addAttribute("course", course);

        return "student/course-detail";
    }
}