import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../css/ExamDetail.css'; // File CSS của bạn ở bên dưới

export default function ExamDetailPage() {
    const { examId } = useParams();
    const navigate = useNavigate();
    const { user } = useAuth();

    const [exam, setExam] = useState(null);
    const [attempts, setAttempts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState('');
    const [file, setFile] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!user || !user.id) {
            setErrorMsg("Không tìm thấy thông tin đăng nhập sinh viên.");
            setLoading(false);
            return;
        }

        const isProd = import.meta.env.PROD;
        const BASE_URL = isProd ? 'https://exam-web-0jf4.onrender.com' : 'http://localhost:8080';

        setLoading(true);
        setErrorMsg('');

        // Gọi song song 2 API: Chi tiết cấu hình đề thi & Lịch sử các lần làm bài của sinh viên
        Promise.all([
            fetch(`${BASE_URL}/api/student/exams/${examId}`, { credentials: 'include' }).then(res => res.json()),
            fetch(`${BASE_URL}/api/student/exams/${examId}/attempts?userId=${user.id}`, { credentials: 'include' }).then(res => res.json())
        ])
            .then(([examData, attemptsData]) => {
                setExam(examData);
                // API attempts trả về List<ExamAttemptHistoryDTO>
                setAttempts(attemptsData || []);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setErrorMsg("Lỗi hệ thống khi kết nối thông tin bài thi.");
                setLoading(false);
            });
    }, [examId, user]);

    // Tìm điểm cao nhất từ danh sách các lần làm bài (Best Score)
    const getBestScore = () => {
        if (!attempts || attempts.length === 0) return null;
        const scores = attempts
            .map(a => a.score)
            .filter(s => s !== null && s !== undefined);
        if (scores.length === 0) return null;
        return Math.max(...scores).toFixed(1);
    };

    // Định dạng thời gian
    const formatDateTime = (dateStr) => {
        if (!dateStr) return '---';
        const date = new Date(dateStr);
        return `${String(date.getDate()).padStart(2, '0')}/${String(date.getMonth() + 1).padStart(2, '0')}/${date.getFullYear()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`;
    };

    const getRemainingTime = (closeDateStr) => {
        if (!closeDateStr) return '---';

        const now = new Date();
        const closeDate = new Date(closeDateStr);
        const diffMs = closeDate - now; // Khoảng cách tính bằng miligiây

        if (diffMs <= 0) {
            return <strong style={{ color: '#dc3545' }}>Đã hết hạn nộp bài</strong>;
        }

        const days = Math.floor(diffMs / (1000 * 60 * 60 * 24));
        const hours = Math.floor((diffMs % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        const minutes = Math.floor((diffMs % (1000 * 60 * 60)) / (1000 * 60));

        let result = '';
        if (days > 0) result += `${days} ngày `;
        if (hours > 0 || days > 0) result += `${hours} giờ `;
        result += `${minutes} phút`;

        return <strong style={{ color: '#ffc107' }}>{result}</strong>; // màu vàng cam nổi bật
    };

    // Xử lý nộp bài tập file tự luận (FILE_UPLOAD)
    const handleFileUploadSubmit = async (e) => {
        e.preventDefault();
        if (!file) return;

        setIsSubmitting(true);
        const formData = new FormData();
        formData.append('file', file);

        const isProd = import.meta.env.PROD;
        const BASE_URL = isProd ? 'https://exam-web-0jf4.onrender.com' : 'http://localhost:8080';

        try {
            const response = await fetch(`${BASE_URL}/api/student/exams/${examId}/submit?userId=${user.id}`, {
                method: 'POST',
                credentials: 'include',
                body: formData
            });

            if (response.ok) {
                alert("Nộp bài tập file thành công!");
                window.location.reload(); // Reload lại dữ liệu trạng thái mới nhất
            } else {
                alert("Nộp bài thất bại. Vui lòng thử lại.");
            }
        } catch (error) {
            console.error(error);
            alert("Lỗi kết nối máy chủ khi nộp file.");
        } finally {
            setIsSubmitting(false);
        }
    };

    if (loading) return <div className="page-container" style={{ color: '#fff', padding: '20px' }}>Đang tải chi tiết kỳ thi...</div>;
    if (errorMsg) return <div className="page-container" style={{ color: 'red', padding: '20px' }}>{errorMsg}</div>;
    if (!exam) return <div className="page-container" style={{ color: '#fff', padding: '20px' }}>Không tìm thấy thông tin kỳ thi.</div>;

    const isFileUpload = exam.examType === 'FILE_UPLOAD';
    const isMultipleChoice = exam.examType === 'MULTIPLE_CHOICE';
    const bestScore = getBestScore();

    return (
        <div className="page-container">
            {/* HEADER */}
            <div className="page-header">
                <h2>{exam.examName || 'Chi tiết bài thi'}</h2>
            </div>

            <div className="content-card">
                <h3>Hướng dẫn</h3>
                <div className="instruction-list">
                    {exam.description ? (
                        <ul style={{ margin: 0, paddingLeft: '20px', listStyleType: 'disc' }}>
                            {exam.description.split('\n').map((line, index) => {
                                // Loại bỏ khoảng trắng thừa ở hai đầu dòng text
                                const trimmedLine = line.trim();
                                // Nếu dòng rỗng thì bỏ qua không render
                                if (!trimmedLine) return null;

                                return (
                                    <li key={index} style={{ marginBottom: '8px' }}>
                                        {trimmedLine}
                                    </li>
                                );
                            })}
                        </ul>
                    ) : (
                        'Không có mô tả hoặc hướng dẫn cụ thể nào dành cho bài thi này.'
                    )}
                </div>
            </div>

            {/* THÔNG TIN CHUNG */}
            <div className="content-card">
                <div className="detail-grid">
                    {/* CỘT TRÁI */}
                    <div className="info-box-column">
                        <h3>Thông tin mốc thời gian</h3>
                        <div className="status-row">
                            <span>Mở từ:</span>
                            <strong>{formatDateTime(exam.openDate)}</strong>
                        </div>
                        <div className="status-row">
                            <span>Đóng lúc:</span>
                            <strong>{formatDateTime(exam.closeDate)}</strong>
                        </div>
                        {/* DÒNG MỚI ĐƯỢC THÊM VÀO ĐÂY */}
                        <div className="status-row">
                            <span>Thời gian còn lại:</span>
                            {getRemainingTime(exam.closeDate)}
                        </div>
                    </div>

                    {/* CỘT PHẢI */}
                    <div className="info-box-column">
                        {/* FILE UPLOAD */}
                        {isFileUpload && (
                            <>
                                <h3>Trạng thái nộp bài</h3>
                                <div className="status-row">
                                    <span>Trạng thái:</span>
                                    <strong style={{ color: attempts.length > 0 ? '#28a745' : '#dc3545' }}>
                                        {attempts.length > 0 ? 'Đã nộp' : 'Chưa nộp'}
                                    </strong>
                                </div>
                                <div className="status-row">
                                    <span>Điểm:</span>
                                    <strong>
                                        {attempts.length > 0 && attempts[0].score !== null ? attempts[0].score : 'Chưa chấm'}
                                    </strong>
                                </div>
                            </>
                        )}

                        {/* MULTIPLE CHOICE */}
                        {isMultipleChoice && (
                            <>
                                <h3>Thông tin cấu trúc bài trắc nghiệm</h3>
                                <div className="status-row">
                                    <span>Số lần làm tối đa:</span>
                                    <strong>{exam.maxAttempts || 'Không giới hạn'}</strong>
                                </div>
                                <div className="status-row">
                                    <span>Thời gian giới hạn:</span>
                                    <strong>{exam.durationMinutes} phút</strong>
                                </div>
                                <div className="status-row">
                                    <span>Cách tính điểm tổng:</span>
                                    <strong>Lần cao nhất (Highest)</strong>
                                </div>
                            </>
                        )}
                    </div>
                </div>
            </div>

            {/* ===================================================== */}
            {/* VÙNG ACTION CHO FILE UPLOAD */}
            {/* ===================================================== */}
            {isFileUpload && (
                <div className="content-card">
                    <h3>Nộp bài tập tự luận</h3>
                    <form onSubmit={handleFileUploadSubmit}>
                        <div className="upload-box">
                            <input
                                type="file"
                                accept=".pdf,.doc,.docx,.zip"
                                onChange={(e) => setFile(e.target.files[0])}
                                required
                            />
                        </div>
                        <button type="submit" className="btn-submit" disabled={isSubmitting}>
                            {isSubmitting ? 'Đang gửi file...' : 'Nộp bài'}
                        </button>
                    </form>
                </div>
            )}

            {/* ===================================================== */}
            {/* VÙNG ACTION CHO MULTIPLE CHOICE */}
            {/* ===================================================== */}
            {isMultipleChoice && (
                <div className="content-card">
                    <div className="quiz-action" style={{ display: 'flex', flexDirection: "column", alignItems: "flex-start", gap: "15px" }}>
                        <div>
                            <h3>Bài kiểm tra trắc nghiệm hệ thống</h3>
                            <p style={{ margin: '4px 0 0', color: '#666', fontSize: '13px' }}>
                                Đề thi gồm {exam.questionAmount || 0} câu hỏi làm trên màn hình khóa.
                            </p>
                        </div>
                        <button
                            className="btn-submit"
                            onClick={() => navigate(`/exams/${exam.examId}/start`)}
                        >
                            Bắt đầu làm bài
                        </button>
                    </div>
                </div>
            )}

            {isMultipleChoice && (
                <div className="content-card">
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '10px', marginBottom: '15px' }}>
                        <h3 style={{ margin: 0 }}>Tổng quan các lần làm bài trước</h3>

                        {/* Đã đẩy Điểm tổng kết lên vị trí này */}
                        {bestScore && (
                            <div className="final-score" style={{ fontSize: '15px' }}>
                                Điểm tổng kết hệ thống:&nbsp;
                                <strong style={{ color: '#4a6cf7', fontSize: '22px' }}>{bestScore}</strong>
                            </div>
                        )}
                    </div>

                    <table className="attempt-table">
                        <thead>
                        <tr>
                            <th>Lần làm</th>
                            <th>Trạng thái & Thời gian nộp</th>
                            <th>Điểm số</th>
                            <th>Xem lại</th>
                        </tr>
                        </thead>
                        <tbody>
                        {attempts.length === 0 ? (
                            <tr>
                                <td colSpan="4" style={{ textAlign: 'center', padding: '20px', color: '#888' }}>
                                    Chưa có lần làm bài nào ghi nhận trên hệ thống.
                                </td>
                            </tr>
                        ) : (
                            attempts.map((attempt, idx) => (
                                <tr key={attempt.examHistoryId || idx}>
                                    <td>{attempt.attemptNumber || (idx + 1)}</td>
                                    <td>
                                        <div style={{ fontWeight: '600', color: '#28a745' }}>Đã hoàn thành</div>
                                        <small style={{ color: '#888' }}>{formatDateTime(attempt.submittedAt)}</small>
                                    </td>
                                    <td style={{ fontWeight: '700', fontSize: '15px' }}>
                                        {attempt.score !== null && attempt.score !== undefined ? attempt.score.toFixed(1) : 'Chưa chấm'}
                                    </td>
                                    <td>
                                        <button
                                            className="btn-review-link"
                                            onClick={() => navigate(`/history/attempt/${attempt.examHistoryId}`)}
                                        >
                                            Xem lại
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
}