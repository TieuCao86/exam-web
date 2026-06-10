package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.ExamAnswerReviewDTO;
import com.exam.exam_web.dto.ExamAttemptHistoryDTO;
import com.exam.exam_web.dto.ExamAttemptResultDTO;
import com.exam.exam_web.dto.ExamHistorySummaryDTO;
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
import java.util.Comparator;
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
    public ExamAttemptResultDTO findAttemptResult(String examHistoryId) {
        // 1. Tìm bản ghi lịch sử gốc
        ExamHistory history = examHistoryRepository.findById(examHistoryId)
                .orElseThrow(() -> new RuntimeException("History not found"));

        // 2. Chạy qua Mapper của bạn để lấy khung DTO cơ bản (Lúc này answers và correctAnswers vẫn đang null)
        ExamAttemptResultDTO dto = attemptResultMapper.toDTO(history);

        // 3. Chủ động bốc danh sách câu trả lời chi tiết từ bảng exam_answers lên
        List<ExamAnswer> examAnswers = examAnswerRepository.findByExamHistory_ExamHistoryId(examHistoryId);

        // 4. Đếm số câu đúng thực tế từ DB
        int correctAnswersCount = (int) examAnswers.stream().filter(ExamAnswer::isCorrect).count();

        // 5. Ánh xạ danh sách ExamAnswer sang ExamAnswerReviewDTO cho Front-end hiển thị
        List<ExamAnswerReviewDTO> reviewList = examAnswers.stream().map(ea ->
                ExamAnswerReviewDTO.builder()
                        .orderInExam(examAnswers.indexOf(ea) + 1) // Hoặc trường thứ tự nếu bạn có lưu
                        .questionContent(ea.getQuestionSnapshot())
                        .selectedAnswer(ea.getSelectedAnswerSnapshot())
                        .correct(ea.isCorrect())
                        .grade(ea.getGrade())
                        // Vì ta lưu snapshot câu trả lời của học sinh, nếu sai có thể hiển thị câu chữ thông báo
                        .correctAnswer(ea.isCorrect() ? ea.getSelectedAnswerSnapshot() : "Mời xem lại tài liệu")
                        .build()
        ).toList();

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
    public ExamAttemptResultDTO submitExam(String userId, String examId, int[] selectedIndexes, int elapsedSeconds) {

        ExamHistory history = examHistoryRepository.findCurrentAttempt(userId, examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lượt làm bài hợp lệ cho sinh viên này"));

        LocalDateTime now = LocalDateTime.now();
        // Bọc lót nếu trường createdAt của dữ liệu cũ bị null, tránh lỗi sập hệ thống
        LocalDateTime startTime = history.getCreatedAt() != null ? history.getCreatedAt() : now.minusSeconds(elapsedSeconds);

        // Tính toán thời gian tối đa cho phép: Thời gian đề cấu hình (phút) * 60 + 30 giây bù trễ mạng
        int maxTimeAllowedSeconds = history.getExam().getDurationMinutes() * 60 + 30;

        // Tính khoảng chênh lệch giây thực tế từ lúc bắt đầu đến lúc nhấn nộp thực tế ở server
        long actualElapsedSeconds = java.time.Duration.between(startTime, now).getSeconds();

        boolean isOvertime = actualElapsedSeconds > maxTimeAllowedSeconds;

        // 2. Lấy danh sách câu hỏi của đề thi này theo đúng thứ tự orderIndex
        List<ExamQuestion> examQuestions = examQuestionRepository.findByExamExamIdOrderByOrderIndexAsc(examId);

        int totalQuestions = history.getExam().getQuestionAmount();
        int correctAnswersCount = 0;
        List<ExamAnswerReviewDTO> reviewList = new ArrayList<>();

        // 3. Duyệt qua từng câu hỏi trong đề để đối chiếu với mảng câu trả lời selectedIndexes
        for (int i = 0; i < examQuestions.size(); i++) {
            ExamQuestion eq = examQuestions.get(i);
            Question question = eq.getQuestion();
            List<Answer> answers = question.getAnswers();

            int chosenIndex = (selectedIndexes != null && i < selectedIndexes.length) ? selectedIndexes[i] : -1;

            Answer selectedAnswer = null;
            boolean isCorrect = false;
            double questionGrade = 0.0;

            if (chosenIndex >= 0 && chosenIndex < answers.size()) {
                selectedAnswer = answers.get(chosenIndex);

                //Nếu đáp án đúng VÀ bài làm KHÔNG bị quá giờ thì mới được ăn điểm
                if (selectedAnswer != null && selectedAnswer.isCorrect()) {
                    if (!isOvertime) {
                        isCorrect = true;
                        correctAnswersCount++;
                        questionGrade = 10.0 / totalQuestions;
                    } else {
                        isCorrect = false; // Quá giờ thì dù chọn đúng vẫn tính là false
                    }
                }
            }

            String correctAnswerText = answers.stream()
                    .filter(Answer::isCorrect)
                    .map(Answer::getContent)
                    .findFirst()
                    .orElse("");

            // 4. Lưu vết câu trả lời chi tiết vào bảng exam_answers
            ExamAnswer examAnswer = ExamAnswer.builder()
                    .examHistory(history)
                    .questionId(question.getQuestionId())
                    .correct(isCorrect)
                    .grade(questionGrade)
                    .questionSnapshot(question.getContent())
                    .selectedAnswerSnapshot(selectedAnswer != null ? selectedAnswer.getContent() : "Không trả lời")
                    .build();
            examAnswerRepository.save(examAnswer);

            // 5. Thêm thông tin vào danh sách trả về hiển thị kết quả
            ExamAnswerReviewDTO review = ExamAnswerReviewDTO.builder()
                    .orderInExam(i + 1)
                    .questionContent(question.getContent())
                    .selectedAnswer(selectedAnswer != null ? selectedAnswer.getContent() : "Không trả lời")
                    .correctAnswer(correctAnswerText)
                    .correct(isCorrect)
                    .grade(questionGrade)
                    .build();
            reviewList.add(review);
        }

        // 6. Tính toán điểm số tổng hợp và làm tròn đến 2 chữ số thập phân
        double finalScore = ((double) correctAnswersCount / totalQuestions) * 10;
        finalScore = Math.round(finalScore * 100.0) / 100.0;

        // 7. Cập nhật lại bản ghi lịch sử thi
        history.setScore(finalScore);
        // Nếu quá giờ, ghi nhận lại thời gian thực tế chạy lố của sinh viên, ngược lại lấy từ FE gửi lên
        history.setElapsedSeconds(isOvertime ? (int) actualElapsedSeconds : elapsedSeconds);
        history.setSubmittedAt(now);
        examHistoryRepository.save(history);

        // 8. Trả về thông tin đầy đủ kết quả chấm điểm
        return ExamAttemptResultDTO.builder()
                .examHistoryId(history.getExamHistoryId())
                .examName(history.getExam().getExamName())
                .attemptNumber(history.getAttemptNumber())
                .score(finalScore)
                .elapsedSeconds(history.getElapsedSeconds())
                .submittedAt(history.getSubmittedAt())
                .totalQuestions(totalQuestions)
                .correctAnswers(correctAnswersCount)
                .answers(reviewList)
                .build();
    }

    // ================= START EXAM FIXES =================
    @Override
    public ExamAttemptHistoryDTO startExam(String userId, String examId) {

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

        ExamHistory saved = examHistoryRepository.save(history);

        return examAttemptHistoryMapper.toDTO(saved);
    }

    @Override
    public int getRemainingSeconds(String userId, String examId) {
        // 1. Tìm lượt thi hiện tại đang diễn ra (chưa nộp bài) của sinh viên
        ExamHistory history = examHistoryRepository.findCurrentAttempt(userId, examId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lượt làm bài nào đang diễn ra cho đề thi này"));

        // Nếu bài thi này đã có mốc nộp bài, thời gian còn lại bằng 0
        if (history.getSubmittedAt() != null) {
            return 0;
        }

        // 2. Lấy mốc thời gian bắt đầu (createdAt)
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


    private double calculateScore(String examId, int[] selectedAnswerIndexes) {
        List<Question> questions = questionRepository.findByExamId(examId);
        double score = 0;

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            Answer correct = answerRepository.findByQuestion_QuestionIdAndIsCorrectTrue(q.getQuestionId());
            List<Answer> answers = answerRepository.findByQuestion_QuestionId(q.getQuestionId());

            if (selectedAnswerIndexes[i] >= 0 && selectedAnswerIndexes[i] < answers.size()) {
                Answer selected = answers.get(selectedAnswerIndexes[i]);
                if (selected.getAnswerId().equals(correct.getAnswerId())) {
                    score += 1;
                }
            }
        }
        return score;
    }
}