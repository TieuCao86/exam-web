import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../css/CourseDetail.css';

export default function CourseDetailPage() {
    const { courseId } = useParams();
    const navigate = useNavigate();
    const { user, logout } = useAuth(); // Lấy hàm logout từ AuthContext để dọn sạch session rác

    const [course, setCourse] = useState(null);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState('');

    useEffect(() => {
        if (!user || !user.id) {
            setErrorMsg("Không tìm thấy thông tin đăng nhập.");
            setLoading(false);
            return;
        }

        const isProd = import.meta.env.PROD;
        const BASE_URL = isProd ? 'https://exam-web-0jf4.onrender.com' : 'http://localhost:8080';

        setLoading(true);
        setErrorMsg('');

        fetch(`${BASE_URL}/api/student/courses/${courseId}?userId=${user.id}`, {
            method: 'GET',
            credentials: 'include',
            headers: {
                'Accept': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
            .then(async (res) => {
                // CHẶN ĐỨT LỖI TREO GIAO DIỆN KHI RESTART SERVER BACKEND:
                if (res.status === 401 || res.status === 403) {
                    console.warn("Phiên đăng nhập hết hạn do máy chủ khởi động lại. Tự động chuyển hướng...");
                    logout(); // Xóa sạch thông tin cũ trong localStorage và đưa user về null
                    navigate('/login'); // Ép chuyển hướng về màn hình đăng nhập
                    return null;
                }

                if (!res.ok) throw new Error("Không thể tải thông tin khóa học.");
                return res.json();
            })
            .then(data => {
                if (data) { // Nếu không bị đá sang trang login (data !== null)
                    setCourse(data);
                    setLoading(false);
                }
            })
            .catch(err => {
                console.error(err);
                setErrorMsg("Lỗi hệ thống khi tải chi tiết khóa học.");
                setLoading(false);
            });
    }, [courseId, user, navigate, logout]);

    const formatDateTime = (dateStr) => {
        if (!dateStr) return '';
        const date = new Date(dateStr);
        const day = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const year = date.getFullYear();
        const hours = String(date.getHours()).padStart(2, '0');
        const minutes = String(date.getMinutes()).padStart(2, '0');
        return `${day}-${month}-${year} ${hours}:${minutes}`;
    };

    if (loading) return <div className="page-container" style={{ color: '#fff', padding: '20px' }}>Đang tải dữ liệu khóa học...</div>;
    if (errorMsg) return <div className="page-container" style={{ color: 'red', padding: '20px' }}>{errorMsg}</div>;
    if (!course) return <div className="page-container" style={{ color: '#fff', padding: '20px' }}>Không tìm thấy dữ liệu.</div>;

    return (
        <div className="page-container">
            <div className="page-header">
                <h2>{course.courseName || 'Tên khóa học'}</h2>
            </div>

            <div className="content-card">
                <div className="course-detail-layout">

                    {/* ======================= PANEL TRÁI ======================= */}
                    <div className="outline-panel">
                        <h3>NỘI DUNG KHÓA HỌC</h3>

                        {course.chapters && course.chapters.map((chapter, index) => (
                            <div key={chapter.chapterId || index}>
                                <div className={`chapter-item ${index === 0 ? 'active' : ''}`}>
                                    <i className={`fas ${index === 0 ? 'fa-chevron-down' : 'fa-chevron-right'}`}></i>
                                    &nbsp;<span>{chapter.title}</span>
                                </div>

                                {chapter.lessons && chapter.lessons.length > 0 && (
                                    <div className="lesson-list">
                                        {chapter.lessons.map((lesson) => (
                                            <a
                                                key={lesson.lessonId}
                                                href={`/lessons/${lesson.lessonId}`}
                                                onClick={(e) => {
                                                    e.preventDefault();
                                                    navigate(`/lessons/${lesson.lessonId}`);
                                                }}
                                            >
                                                <i className={`fas ${lesson.lessonType === 'VIDEO' ? 'fa-link' : 'fa-file-alt'}`}></i>
                                                &nbsp;<span>{lesson.title}</span>
                                            </a>
                                        ))}
                                    </div>
                                )}
                            </div>
                        ))}

                        <div className="chapter-item active">
                            <i className="fas fa-chevron-down"></i>
                            &nbsp;KIỂM TRA
                        </div>

                        <div className="lesson-list">
                            {course.exams && course.exams.map((exam) => {
                                let prefixIcon = "📄 ";
                                if (exam.examName?.includes("TX1")) prefixIcon = "🟢 ";
                                if (exam.examName?.includes("cuối kỳ")) prefixIcon = "🔒 ";

                                return (
                                    <a
                                        key={exam.examId}
                                        href={`/exams/${exam.examId}`}
                                        onClick={(e) => {
                                            e.preventDefault();
                                            navigate(`/exams/${exam.examId}`);
                                        }}
                                    >
                                        <span>{prefixIcon}</span>
                                        <span>{exam.examName}</span>
                                    </a>
                                );
                            })}
                        </div>
                    </div>

                    {/* ======================= PANEL PHẢI ======================= */}
                    <div className="detail-panel">
                        <div className="section-title">
                            KIỂM TRA
                        </div>

                        {course.exams && course.exams.map((exam) => (
                            <div className="exam-item" key={exam.examId}>
                                <div className="exam-header">
                                    <div className="exam-icon">
                                        <i className="fas fa-file-alt"></i>
                                    </div>
                                    <div>
                                        <small>BÀI TẬP</small>
                                        <h4>{exam.examName}</h4>
                                    </div>
                                </div>

                                <div className="exam-body">
                                    <span>
                                        {exam.examName?.includes("TX1")
                                            ? 'Thực hiện theo file hướng dẫn.'
                                            : `Nội dung kiểm tra môn ${course.courseName || ''}.`}
                                    </span>
                                </div>

                                {exam.closeDate && (
                                    <div className="exam-footer">
                                        <i className="fas fa-lock"></i>
                                        &nbsp;Available until:&nbsp;
                                        <span>{formatDateTime(exam.closeDate)}</span>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>

                </div>
            </div>
        </div>
    );
}