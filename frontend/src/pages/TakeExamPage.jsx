import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../css/TakeExam.css';

export default function TakeExamPage() {
    const { examId } = useParams();
    const navigate = useNavigate();
    const { user, logout } = useAuth(); // Lấy thêm hàm logout từ Context để xóa session rác khi cần

    // State quản lý dữ liệu từ API
    const [exam, setExam] = useState(null);
    const [questions, setQuestions] = useState([]);
    const [currentIdx, setCurrentIdx] = useState(0);
    const [answers, setAnswers] = useState({}); // Cấu trúc: { [questionId]: answerTextOrId }
    const [timeLeft, setTimeLeft] = useState(0);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState('');

    // Dùng Ref để tính số giây đã làm thực tế (elapsedSeconds)
    const initialTimeLeftRef = useRef(0);

    const isProd = import.meta.env.PROD;
    const BASE_URL = isProd ? 'https://exam-web-0jf4.onrender.com' : 'http://localhost:8080';

    useEffect(() => {
        if (!user || !user.id) {
            setErrorMsg("Không tìm thấy thông tin đăng nhập sinh viên.");
            setLoading(false);
            return;
        }

        let isMounted = true; // Cờ flag chống double-fetch gây lỗi khi StrictMode mount 2 lần liên tiếp
        setLoading(true);
        setErrorMsg('');

        const initializeQuizRoom = async () => {
            try {
                // Bước 1: Kích hoạt lượt làm bài (Chấp nhận catch nếu backend báo lỗi bản ghi trùng lặp)
                try {
                    await fetch(`${BASE_URL}/api/student/exams/${examId}/start?userId=${user.id}`, {
                        method: 'POST',
                        credentials: 'include'
                    });
                } catch (e) {
                    console.log("Lượt thi này đã được khởi tạo trước đó.");
                }

                if (!isMounted) return;

                // Bước 2: Gọi đồng thời 3 API chuẩn bị phòng thi
                const [examRes, questionsRes, timeRes] = await Promise.all([
                    fetch(`${BASE_URL}/api/student/exams/${examId}`, { credentials: 'include' }),
                    fetch(`${BASE_URL}/api/student/exams/${examId}/questions`, { credentials: 'include' }),
                    fetch(`${BASE_URL}/api/student/exams/${examId}/time-left?userId=${user.id}`, { credentials: 'include' })
                ]);

                // CHẶN ĐỨT LỖI TREO GIAO DIỆN KHI RESTART SERVER BACKEND:
                if (examRes.status === 401 || examRes.status === 403 || questionsRes.status === 401) {
                    console.warn("Phiên đăng nhập hết hạn do máy chủ khởi động lại. Tự động chuyển hướng...");
                    logout(); // Xóa sạch thông tin cũ rác trong localStorage
                    navigate('/login'); // Đá thẳng sinh viên về trang đăng nhập
                    return;
                }

                if (!examRes.ok) throw new Error("Không thể lấy cấu hình đề thi.");
                if (!questionsRes.ok) throw new Error("Không thể tải danh sách câu hỏi.");

                const examData = await examRes.json();
                const questionsData = await questionsRes.json();

                let secondsLeft = null;
                if (timeRes.ok) {
                    try { secondsLeft = await timeRes.json(); } catch (e) {}
                }

                if (!isMounted) return;

                setExam(examData);
                setQuestions(questionsData || []);

                // Thiết lập thời gian an toàn
                if (secondsLeft !== null && secondsLeft !== undefined && !isNaN(secondsLeft) && secondsLeft > 0) {
                    setTimeLeft(secondsLeft);
                    initialTimeLeftRef.current = secondsLeft;
                } else {
                    const fallbackSeconds = (examData.durationMinutes || 45) * 60;
                    setTimeLeft(fallbackSeconds);
                    initialTimeLeftRef.current = fallbackSeconds;
                    console.warn("Đang sử dụng thời gian mặc định cấu hình đề.");
                }

                setLoading(false);
            } catch (err) {
                console.error(err);
                if (isMounted) {
                    setErrorMsg("Lỗi kết nối hệ thống khi tải đề thi.");
                    setLoading(false);
                }
            }
        };

        initializeQuizRoom();

        return () => {
            isMounted = false; // Hủy cờ khi unmount component
        };
    }, [examId, user, navigate, logout]);

    // Bộ đếm ngược thời gian
    useEffect(() => {
        if (loading || timeLeft <= 0) {
            if (timeLeft === 0 && !loading && questions.length > 0) {
                alert("Hết thời gian làm bài! Hệ thống tự động nộp bài.");
                handleAutoSubmit();
            }
            return;
        }

        const timer = setInterval(() => {
            setTimeLeft(prev => prev - 1);
        }, 1000);

        return () => clearInterval(timer);
    }, [timeLeft, loading, questions.length]);

    // Định dạng thời gian hiển thị (MM:SS)
    const formatTime = (seconds) => {
        if (seconds <= 0) return "00:00";
        const mins = Math.floor(seconds / 60);
        const secs = seconds % 60;
        return `${String(mins).padStart(2, '0')}:${String(secs).padStart(2, '0')}`;
    };

    // Chọn đáp án
    const handleSelectOption = (questionId, selectedValue) => {
        setAnswers(prev => ({
            ...prev,
            [questionId]: selectedValue
        }));
    };

    // Chuẩn bị cấu trúc Body để nộp bài chuẩn định dạng backend mong đợi
    const buildSubmitBody = () => {
        const elapsedSeconds = initialTimeLeftRef.current - timeLeft;

        const userAnswers = Object.keys(answers).map(qId => ({
            questionId: qId, // Giữ nguyên kiểu dữ liệu chuỗi (String) đồng bộ với backend DTO
            selectedAnswerId: answers[qId]
        }));

        return {
            elapsedSeconds: elapsedSeconds > 0 ? elapsedSeconds : 0,
            userAnswers: userAnswers
        };
    };

    // Xử lý nộp bài thủ công
    const handleSubmit = async () => {
        const confirmSubmit = window.confirm("Bạn có chắc chắn muốn nộp bài kiểm tra này không?");
        if (!confirmSubmit) return;

        await submitData();
    };

    // Xử lý tự động nộp khi hết giờ
    const handleAutoSubmit = async () => {
        await submitData();
    };

    // Hàm gọi API submit chung
    const submitData = async () => {
        try {
            const body = buildSubmitBody();
            const response = await fetch(`${BASE_URL}/api/student/exams/${examId}/submit?userId=${user.id}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                credentials: 'include',
                body: JSON.stringify(body)
            });

            if (response.status === 401 || response.status === 403) {
                logout();
                navigate('/login');
                return;
            }

            if (response.ok) {
                alert("Nộp bài thành công!");
                navigate(`/exams/${examId}`); // Điều hướng về trang chi tiết đề thi để xem điểm tổng kết
            } else {
                alert("Nộp bài thất bại. Vui lòng liên hệ giám thị.");
            }
        } catch (error) {
            console.error(error);
            alert("Lỗi kết nối máy chủ khi nộp bài.");
        }
    };

    if (loading) return <div className="quiz-page-wrapper" style={{ color: '#fff', padding: '20px' }}>Đang khởi tạo phòng thi và tải đề bài...</div>;
    if (errorMsg) return <div className="quiz-page-wrapper" style={{ color: '#fff', padding: '20px' }}><div style={{ background: '#fff', color: 'red', padding: '15px', borderRadius: '8px' }}>{errorMsg}</div></div>;
    if (questions.length === 0) return <div className="quiz-page-wrapper" style={{ color: '#fff', padding: '20px' }}>Đề thi hiện tại chưa có câu hỏi nào.</div>;

    const currentQuestion = questions[currentIdx];

    return (
        <div className="quiz-page-wrapper">
            <div className="quiz-container">

                {/* HEADER BÀI THI */}
                <div className="quiz-header">
                    <h2>{exam?.examName || 'Bài kiểm tra trắc nghiệm'}</h2>
                    <div className="countdown-box">
                        <span className="countdown-label">Thời gian còn lại:</span>
                        <strong className="countdown-clock">{formatTime(timeLeft)}</strong>
                    </div>
                </div>

                {/* KHU VỰC CHÍNH */}
                <div className="quiz-main-grid">

                    {/* BÊN TRÁI: NỘI DUNG CÂU HỎI */}
                    <div className="quiz-left-column">
                        <div className="quiz-card card-question-title">
                            <h3>Câu hỏi {currentIdx + 1} / {questions.length}</h3>
                        </div>

                        <div className="quiz-card card-question-body">
                            <p className="question-text">{currentQuestion?.content || currentQuestion?.questionText}</p>

                            <div className="options-list">
                                {currentQuestion?.answers && currentQuestion.answers.length > 0 ? (
                                    currentQuestion.answers.map((opt, index) => {
                                        const optionKey = String.fromCharCode(65 + index);
                                        const optionId = opt.answerId || opt.id;
                                        const isSelected = answers[currentQuestion.questionId] === optionId;

                                        return (
                                            <label
                                                key={optionId || index}
                                                className={`option-item ${isSelected ? 'selected' : ''}`}
                                            >
                                                <input
                                                    type="radio"
                                                    name={`q-${currentQuestion.questionId}`}
                                                    checked={isSelected}
                                                    onChange={() => handleSelectOption(currentQuestion.questionId, optionId)}
                                                />
                                                <span className="option-mark">{optionKey}.</span>
                                                <span className="option-text">{opt.content || opt.text}</span>
                                            </label>
                                        );
                                    })
                                ) : (
                                    <p style={{ color: '#888', fontStyle: 'italic' }}>Không tìm thấy phương án lựa chọn nào cho câu hỏi này.</p>
                                )}
                            </div>
                        </div>

                        {/* ĐIỀU HƯỚNG TRƯỚC / SAU */}
                        <div className="quiz-action-nav">
                            <button
                                className="btn-nav-arrow"
                                disabled={currentIdx === 0}
                                onClick={() => setCurrentIdx(prev => prev - 1)}
                            >
                                Câu trước
                            </button>
                            <button
                                className="btn-nav-arrow"
                                disabled={currentIdx === questions.length - 1}
                                onClick={() => setCurrentIdx(prev => prev + 1)}
                            >
                                Câu tiếp theo
                            </button>
                        </div>
                    </div>

                    {/* BÊN PHẢI: LƯỚI ĐIỀU HƯỚNG */}
                    <div className="quiz-right-column">
                        <div className="quiz-card card-navigation">
                            <h3>Danh sách câu hỏi</h3>
                            <div className="nav-grid">
                                {questions.map((q, idx) => {
                                    const isCurrent = idx === currentIdx;
                                    const isAnswered = answers[q.questionId] !== undefined;

                                    let btnClass = "nav-number-btn";
                                    if (isCurrent) btnClass += " active";
                                    else if (isAnswered) btnClass += " answered";

                                    return (
                                        <button
                                            key={q.questionId}
                                            className={btnClass}
                                            onClick={() => setCurrentIdx(idx)}
                                        >
                                            {idx + 1}
                                        </button>
                                    );
                                })}
                            </div>

                            <hr className="nav-divider" />

                            <button className="btn-quiz-submit" onClick={handleSubmit}>
                                Nộp bài thi
                            </button>
                        </div>
                    </div>

                </div>
            </div>
        </div>
    );
}