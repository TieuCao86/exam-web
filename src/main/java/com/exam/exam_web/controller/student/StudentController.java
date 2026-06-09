package com.exam.exam_web.controller.student;

import com.exam.exam_web.dto.CourseDTO;
import com.exam.exam_web.dto.ExamDTO;
import com.exam.exam_web.dto.OptionDTO;
import com.exam.exam_web.services.CourseService;
import com.exam.exam_web.services.ExamHistoryService;
import com.exam.exam_web.services.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class StudentController {

    private final CourseService courseService;
    private final ExamService examService;
    private final ExamHistoryService examHistoryService;

    // ===================== TEMP USER =====================
    private String getUserId() {
        return "STUDENT001"; // tạm thời chưa dùng security
    }

    // ===================== CALENDAR =====================
    @GetMapping("/calendar")
    public String showCalendar() {
        return "student/calendar";
    }

    // ===================== COURSES LIST =====================
    @GetMapping("/courses")
    public String getCourses(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String semester,
            @RequestParam(required = false) String year,
            Model model
    ) {

        keyword = normalize(keyword);
        semester = normalize(semester);
        year = normalize(year);

        List<CourseDTO> courses = courseService.search(keyword, semester, year);

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

    // ===================== EXAMS LIST =====================
    @GetMapping("/exams")
    public String getExams(Model model) {

        String userId = getUserId();

        model.addAttribute(
                "exams",
                examService.findAvailableExams(userId)
        );

        return "student/exams";
    }

    // ===================== COURSE DETAIL =====================
    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable String id, Model model) {

        CourseDTO course = courseService.findById(id);

        if (course == null) {
            return "redirect:/courses";
        }

        model.addAttribute("course", course);

        return "student/course-detail";
    }

    // ===================== EXAM DETAIL =====================
    @GetMapping("/exams/{id}")
    public String examDetail(@PathVariable String id, Model model) {

        String userId = getUserId(); // "STUDENT001"

        ExamDTO exam = examService.findById(id);

        if (exam == null) {
            return "redirect:/exams";
        }

        // if (!examService.checkParticipate(userId, id)) {
        //     return "redirect:/exams";
        // }

        model.addAttribute("exam", exam);

        // Nạp thêm lịch sử làm bài để tránh lỗi trống bảng (như mình hướng dẫn ở lượt trước)
        var attempts = examHistoryService.findAttempts(userId, id);
        model.addAttribute("attempts", attempts);
        model.addAttribute("bestScore", attempts.stream().mapToDouble(a -> a.getScore() != null ? a.getScore() : 0.0).max().orElse(0.0));

        return "student/exam-detail";
    }

//    @GetMapping("/exams/{id}/start")
//    public String startExam(@PathVariable String id, Model model) {
//
//        String userId = getUserId();
//
//        ExamDTO exam = examService.findById(id);
//
//        if (exam == null) {
//            return "redirect:/exams";
//        }
//
//        String historyId = examHistoryService.startExam(userId, id);
//
//        model.addAttribute("exam", exam);
//        model.addAttribute("historyId", historyId);
//
//        return "student/exam-take";
//    }

    // ===================== HISTORY LIST =====================
    @GetMapping("/history")
    public String history(Model model) {

        String userId = getUserId();

        model.addAttribute(
                "histories",
                examHistoryService.findHistoryByUser(userId)
        );

        return "student/history";
    }

    // ===================== HISTORY BY EXAM =====================
    @GetMapping("/history/{examId}")
    public String historyDetail(@PathVariable String examId, Model model) {

        String userId = getUserId();

        model.addAttribute(
                "attempts",
                examHistoryService.findAttempts(userId, examId)
        );

        ExamDTO exam = examService.findById(examId);

        if (exam != null) {
            model.addAttribute("examName", exam.getExamName());
        }

        return "student/history-detail";
    }

    // ===================== ATTEMPT RESULT =====================
    @GetMapping("/history/attempt/{historyId}")
    public String attemptResult(@PathVariable String historyId, Model model) {

        model.addAttribute(
                "result",
                examHistoryService.findAttemptResult(historyId)
        );

        return "student/attempt-result";
    }

    // ===================== UTILITY =====================
    private String normalize(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}