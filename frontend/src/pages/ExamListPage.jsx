import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../css/ExamList.css'; // File CSS của bạn ở bên dưới

export default function ExamListPage() {
    const navigate = useNavigate();
    const { user } = useAuth();

    const [exams, setExams] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState('');

    // Quản lý tab trạng thái: 'available', 'upcoming', 'expired'
    const [activeTab, setActiveTab] = useState('available');

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

        // Khớp endpoint động dựa vào Tab trạng thái người dùng chọn
        fetch(`${BASE_URL}/api/student/exams/${activeTab}/page?userId=${user.id}&page=${currentPage}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(res => {
                if (!res.ok) throw new Error("Không thể tải danh sách kỳ thi.");
                return res.json();
            })
            .then(data => {
                setExams(data.content || []);
                setTotalPages(data.totalPages || 1);
                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setErrorMsg("Lỗi hệ thống khi kết nối dữ liệu lịch thi.");
                setLoading(false);
            });
    }, [user, currentPage, activeTab]); // Tự động reload khi đổi trang hoặc đổi Tab lọc

    const handleTabChange = (e) => {
        setActiveTab(e.target.value);
        setCurrentPage(0); // Reset về trang 1 khi đổi tab lọc
    };

    if (loading) return <div className="page-container" style={{ color: '#fff', padding: '20px' }}>Đang tải danh sách kỳ thi...</div>;
    if (errorMsg) return <div className="page-container" style={{ color: 'red', padding: '20px' }}>{errorMsg}</div>;

    return (
        <div className="page-container">

            {/* HEADER */}
            <div className="page-header">
                <h2>CÁC KÌ THI CỦA BẠN</h2>
            </div>

            {/* ACTION BAR LỌC TRẠNG THÁI (Mapping trực tiếp với API của bạn) */}
            <div className="action-bar">
                <div>
                    <label>
                        <select className="filter-select" value={activeTab} onChange={handleTabChange}>
                            <option value="available">🟢 Đang mở cổng (Available)</option>
                            <option value="upcoming">⏳ Sắp diễn ra (Upcoming)</option>
                            <option value="expired">🔒 Đã đóng cổng (Expired)</option>
                        </select>
                    </label>
                </div>

                <div>
                    <label>
                        <select className="filter-select" disabled>
                            <option>Môn học (Tất cả)</option>
                        </select>
                    </label>
                </div>
            </div>

            {/* CONTENT CARD */}
            <div className="content-card">
                {exams.length === 0 ? (
                    <div style={{ padding: '40px', textAlign: 'center', color: '#666' }}>
                        Không có bài thi nào trong mục này.
                    </div>
                ) : (
                    <>
                        {/* GRID DANH SÁCH BÀI THI */}
                        <div className="exam-grid">
                            {exams.map((exam) => (
                                <div className="exam-card" key={exam.examId}>

                                    <div className="exam-title">
                                        <h3>{exam.examName}</h3>
                                    </div>

                                    <div className="exam-info">
                                        <div className="info-item">
                                            <i className="fas fa-clock"></i>
                                            <span>{exam.durationMinutes} phút</span>
                                        </div>

                                        <div className="info-item">
                                            <i className="fas fa-question-circle"></i>
                                            <span>{exam.questionAmount || 0} câu hỏi</span>
                                        </div>
                                    </div>

                                    <div className="exam-tags">
                                        <span className="tag">#{exam.subjectName || exam.courseName || 'Kỳ thi Sinh viên'}</span>
                                        <span className="tag" style={{ background: '#fef3c7', color: '#d97706' }}>
                                            {exam.examType === 'MULTIPLE_CHOICE' ? 'Trắc nghiệm' : 'Tự luận'}
                                        </span>
                                    </div>

                                    <div className="exam-footer">
                                        <button
                                            className="btn-detail-link"
                                            onClick={() => navigate(`/exams/${exam.examId}`)}
                                        >
                                            Chi tiết
                                        </button>
                                    </div>

                                </div>
                            ))}
                        </div>

                        {/* THANH PHÂN TRANG (PAGINATION SYSTEM) */}
                        {totalPages > 1 && (
                            <div className="pagination-bar" style={{ display: 'flex', gap: '8px', marginTop: '20px', justifyContent: 'center' }}>
                                <button
                                    className="nav-arrow-btn"
                                    disabled={currentPage === 0}
                                    onClick={() => setCurrentPage(prev => prev - 1)}
                                >
                                    &laquo; Trước
                                </button>
                                <span style={{ color: '#fff', alignSelf: 'center', fontSize: '14px' }}>
                                    Trang {currentPage + 1} / {totalPages}
                                </span>
                                <button
                                    className="nav-arrow-btn"
                                    disabled={currentPage >= totalPages - 1}
                                    onClick={() => setCurrentPage(prev => prev + 1)}
                                >
                                    Sau &raquo;
                                </button>
                            </div>
                        )}
                    </>
                )}
            </div>

        </div>
    );
}