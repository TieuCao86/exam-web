package com.exam.exam_web.services.impl;

import com.exam.exam_web.api.student.StudentExamController;
import com.exam.exam_web.dto.*;
import com.exam.exam_web.entity.*;
import com.exam.exam_web.mapper.ExamAttemptHistoryMapper;
import com.exam.exam_web.mapper.ExamAttemptResultMapper;
import com.exam.exam_web.mapper.ExamHistorySummaryMapper;
import com.exam.exam_web.repository.*;
import com.exam.exam_web.services.ExamHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Đã thêm import này

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExamHistoryServiceImpl implements ExamHistoryService {

    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final ExamHistoryRepository examHistoryRepository;
    private final ExamAnswerRepository examAnswerRepository;
    private final ExamQuestionRepository examQuestionRepository;

    private final ExamHistorySummaryMapper summaryMapper;
    private final ExamAttemptHistoryMapper attemptHistoryMapper;
    private final ExamAttemptResultMapper attemptResultMapper;
    private final ExamAttemptHistoryMapper examAttemptHistoryMapper;

    @Override
    public List<ExamHistorySummaryDTO> findHistoryByUser(String userId) {
        return examHistoryRepository
                .findByUser_UserId(userId)
                .stream()
                .collect(
                        java.util.stream.Collectors.groupingBy(
                                h -> h.getExam().getExamId()
                        )
                )
                .values()
                .stream()
                .map(list -> {
                    ExamHistory latest = list.stream()
                            .max((a, b) -> a.getSubmittedAt().compareTo(b.getSubmittedAt()))
                            .orElseThrow();

                    double bestScore = list.stream()
                            .mapToDouble(ExamHistory::getScore)
                            .max()
                            .orElse(0);

                    return ExamHistorySummaryDTO.builder()
                            .examId(latest.getExam().getExamId())
                            .examName(latest.getExam().getExamName())
                            .subjectName(latest.getExam().getCourse().getSubject().getName())
                            .totalAttempts((long) list.size())
                            .bestScore(bestScore)
                            .lastSubmittedAt(latest.getSubmittedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public List<ExamAttemptHistoryDTO> findAttempts(String userId, String examId) {
        return examHistoryRepository
                .findByUser_UserIdAndExam_ExamId(userId, examId)
                .stream()
                .map(attemptHistoryMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true) // Thêm readOnly để tối ưu hóa tốc độ đọc dữ liệu của Hibernate
    public ExamAttemptResultDTO findAttemptResult(String examHistoryId) {
        // 1. Tìm bản ghi lịch sử gốc
        ExamHistory history = examHistoryRepository.findById(examHistoryId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        // 2. Chạy qua Mapper lấy khung DTO cơ bản
        ExamAttemptResultDTO dto = attemptResultMapper.toDTO(history);

        // 3. Chủ động bốc danh sách câu trả lời chi tiết từ bảng exam_answers lên
        List<ExamAnswer> examAnswers = examAnswerRepository.findByExamHistory_ExamHistoryId(examHistoryId);

        // 4. Đếm số câu đúng thực tế từ DB
        int correctAnswersCount = (int) examAnswers.stream().filter(ExamAnswer::isCorrect).count();

        // 5. Ánh xạ danh sách sang DTO bằng vòng lặp thông thường
        List<ExamAnswerReviewDTO> reviewList = new ArrayList<>();
        int index = 1; // Biến đếm số nguyên thuần túy

        for (ExamAnswer ea : examAnswers) {
            ExamAnswerReviewDTO review = ExamAnswerReviewDTO.builder()
                    .orderInExam(index++) // Gán xong tự động tăng lên 1
                    .questionContent(ea.getQuestionSnapshot())
                    .selectedAnswer(ea.getSelectedAnswerSnapshot())
                    .correct(ea.isCorrect())
                    .grade(ea.getGrade())
                    .correctAnswer(ea.isCorrect() ? ea.getSelectedAnswerSnapshot() : "Mời xem lại tài liệu")
                    .build();

            reviewList.add(review);
        }

        // 6. Đập dữ liệu vừa tính toán vào DTO trước khi trả về
        dto.setCorrectAnswers(correctAnswersCount);
        dto.setAnswers(reviewList);

        return dto;
    }

    @Override
    public Integer getMaxAttemptNumber(String userId, String examId) {
        return examHistoryRepository
                .findByUser_UserIdAndExam_ExamId(userId, examId)
                .stream()
                .map(ExamHistory::getAttemptNumber)
                .max(Integer::compareTo)
                .orElse(0);
    }

    @Override
    @Transactional
    public ExamAttemptResultDTO submitExam(String examId, String userId, StudentExamController.ExamSubmitBody body) {
        ExamHistory history = examHistoryRepository.findCurrentAttempt(userId, examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lượt làm bài nào đang diễn ra (chưa nộp) cho đề thi này!"));

        Exam exam = history.getExam();
        int totalQuestions = exam.getQuestionAmount();
        double pointsPerQuestion = 10.0 / totalQuestions;

        int correctAnswersCount = 0;
        double totalGrade = 0.0;
        List<ExamAnswer> examAnswersToSave = new ArrayList<>();

        for (AnswerSelectionDTO ansSelection : body.getUserAnswers()) {
            Question question = questionRepository.findById(ansSelection.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy câu hỏi: " + ansSelection.getQuestionId()));

            String questionText = question.getContent();
            String selectedAnswerText = "Học viên bỏ trống câu này";
            boolean isCorrect = false;
            double questionGrade = 0.0;

            if (ansSelection.getSelectedAnswerId() != null && !ansSelection.getSelectedAnswerId().trim().isEmpty()) {
                Answer selectedAnswer = answerRepository.findById(ansSelection.getSelectedAnswerId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy đáp án có ID: " + ansSelection.getSelectedAnswerId()));

                selectedAnswerText = selectedAnswer.getContent();

                if (selectedAnswer.isCorrect()) {
                    isCorrect = true;
                    correctAnswersCount++;
                    questionGrade = pointsPerQuestion;
                    totalGrade += pointsPerQuestion;
                }
            }

            ExamAnswer examAnswer = ExamAnswer.builder()
                    .examHistory(history)
                    .questionId(ansSelection.getQuestionId())
                    .correct(isCorrect)
                    .grade(questionGrade)
                    .questionSnapshot(questionText)
                    .selectedAnswerSnapshot(selectedAnswerText)
                    .build();

            examAnswersToSave.add(examAnswer);
        }

        examAnswerRepository.saveAll(examAnswersToSave);

        history.setScore(totalGrade);
        history.setElapsedSeconds(body.getElapsedSeconds());
        history.setSubmittedAt(LocalDateTime.now());
        examHistoryRepository.save(history);

        return findAttemptResult(history.getExamHistoryId());
    }

    @Override
    public List<ExamAttemptHistoryDTO> findByExamId(String examId) {
        return examHistoryRepository.findByExam_ExamId(examId)
                .stream()
                .map(attemptHistoryMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true) // Tối ưu hóa hiệu năng đọc dữ liệu từ DB
    public List<TeacherSubmissionDTO> getSubmissionsByExamId(String examId) {
        return examHistoryRepository.findSubmissionsByExamId(examId);
    }

    // ================= START EXAM =================
    @Override
    @Transactional
    public ExamAttemptHistoryDTO startExam(String userId, String examId) {

        // Kiểm tra an toàn bọc lót tầng dưới (Mặc dù Controller đã khóa đồng bộ)
        java.util.Optional<ExamHistory> currentAttempt = examHistoryRepository.findCurrentAttempt(userId, examId);
        if (currentAttempt.isPresent()) {
            return examAttemptHistoryMapper.toDTO(currentAttempt.get());
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        int nextAttempt = getMaxAttemptNumber(userId, examId) + 1;

        ExamHistory history = new ExamHistory();
        history.setUser(user);
        history.setExam(exam);
        history.setAttemptNumber(nextAttempt);
        history.setSubmittedAt(null);
        history.setScore(0.0);
        history.setCreatedAt(LocalDateTime.now()); // Không được để NULL tránh lỗi 500 khi tính toán thời gian còn lại
        history.setCheatCount(0);

        ExamHistory saved = examHistoryRepository.save(history);

        return examAttemptHistoryMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public int getRemainingSeconds(String userId, String examId) {
        // 1. Tìm lượt thi hiện tại đang diễn ra (chưa nộp bài) của sinh viên
        // Nếu không thấy (có thể do StrictMode hủy luồng hoặc lỗi DB), bọc lót trả về thời gian gốc của đề thay vì ném lỗi 500
        ExamHistory history = examHistoryRepository.findCurrentAttempt(userId, examId).orElse(null);

        if (history == null) {
            Exam exam = examRepository.findById(examId).orElse(null);
            return exam != null ? exam.getDurationMinutes() * 60 : 45 * 60;
        }

        // Nếu bài thi này đã có mốc nộp bài, thời gian còn lại bằng 0
        if (history.getSubmittedAt() != null) {
            return 0;
        }

        // 2. Lấy mốc thời gian bắt đầu
        java.time.LocalDateTime startTime = history.getCreatedAt();
        if (startTime == null) {
            return history.getExam().getDurationMinutes() * 60; // Bọc lót dữ liệu lỗi
        }

        // 3. Tính toán chênh lệch
        int totalDurationSeconds = history.getExam().getDurationMinutes() * 60;
        long secondsPassed = java.time.Duration.between(startTime, java.time.LocalDateTime.now()).getSeconds();

        int timeLeft = totalDurationSeconds - (int) secondsPassed;

        // Nếu kết quả âm (quá giờ) thì trả về 0, ngược lại trả về số giây thực tế còn lại
        return Math.max(timeLeft, 0);
    }

    @Override
    public boolean checkEligibility(String userId, String examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đề thi"));

        //Kiểm tra xem thời gian hiện tại có nằm trong khung giờ mở đề không
        LocalDateTime now = LocalDateTime.now();
        if (exam.getOpenDate() != null && now.isBefore(exam.getOpenDate())) {
            return false; // Chưa đến giờ mở đề
        }
        if (exam.getCloseDate() != null && now.isAfter(exam.getCloseDate())) {
            return false; // Đã quá hạn thi
        }

        // Tận dụng hàm getMaxAttemptNumber có sẵn trong Service của bạn để lấy số lượt đã làm
        Integer currentAttempts = this.getMaxAttemptNumber(userId, examId);
        int completedAttempts = (currentAttempts != null) ? currentAttempts : 0;

        // Nếu có cấu hình số lượt tối đa và sinh viên đã làm hết lượt -> Chặn
        return exam.getMaxAttempts() == null || completedAttempts < exam.getMaxAttempts();// Hợp lệ
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void increaseCheatCount(String userId, String examId) {
        // 1. Tìm lượt thi hiện tại đang làm của sinh viên
        ExamHistory history = examHistoryRepository.findCurrentAttempt(userId, examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lượt làm bài hợp lệ để ghi nhận vi phạm"));

        // Nếu bài đã nộp rồi thì không tăng nữa
        if (history.getSubmittedAt() != null) {
            return;
        }

        // 2. Tăng số lần vi phạm lên 1
        int newCheatCount = history.getCheatCount() + 1;
        history.setCheatCount(newCheatCount);

        // 3. BIỆN PHÁP MẠNH: Nếu vi phạm từ 3 lần trở lên, tự động HỦY BÀI / THU BÀI NGAY LẬP TỨC với 0 điểm
        if (newCheatCount >= 3) {
            history.setScore(0.0);
            history.setSubmittedAt(java.time.LocalDateTime.now());
            // Bạn có thể ghi thêm log hoặc note vào DB nếu bảng có trường ghi chú
        }

        examHistoryRepository.save(history);
    }
}