import React, { useEffect, useState } from 'react';
import '../css/Calendar.css';
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext.jsx";

const Calendar = ({ currentMonthYear, calendarDays }) => {
    const navigate = useNavigate();
    const { user } = useAuth();
    const [exams, setExams] = useState([]);
    const [loading, setLoading] = useState(true);
    const [errorMsg, setErrorMsg] = useState('');

    useEffect(() => {
        if (!user || !user.id) {
            setErrorMsg("Không tìm thấy thông tin định danh sinh viên.");
            setLoading(false);
            return;
        }

        const userId = user.id;
        const isProd = import.meta.env.PROD;
        const BASE_URL = isProd ? 'https://exam-web-0jf4.onrender.com' : 'http://localhost:8080';

        setLoading(true);

        Promise.all([
            fetch(
                `${BASE_URL}/api/student/exams/available/page?userId=${userId}&page=0`,
                {
                    credentials: 'include',
                    headers: {
                        Accept: 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                }
            ).then(res => {
                if (!res.ok) throw new Error("Available API: " + res.status);
                return res.json();
            }),

            fetch(
                `${BASE_URL}/api/student/exams/upcoming/page?userId=${userId}&page=0`,
                {
                    credentials: 'include',
                    headers: {
                        Accept: 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                }
            ).then(res => {
                if (!res.ok) throw new Error("Upcoming API: " + res.status);
                return res.json();
            })
        ])
            .then(([availableRes, upcomingRes]) => {
                console.log("AVAILABLE", availableRes);
                console.log("UPCOMING", upcomingRes);

                setExams([
                    ...(availableRes.content || []),
                    ...(upcomingRes.content || [])
                ]);

                setLoading(false);
            })
            .catch(err => {
                console.error(err);
                setErrorMsg("Không thể tải danh sách đề thi từ server.");
                setLoading(false);
            });
    }, [user]);

    const rows = Array.from({ length: 5 }, (_, rowIndex) => rowIndex + 1);
    const daysInRow = Array.from({ length: 7 }, (_, dayIndex) => dayIndex + 1);

    if (loading) {
        return <div className="page-container"><div style={{ padding: '20px', color: '#fff' }}>Đang tải lịch thi thực tế từ hệ thống...</div></div>;
    }

    if (errorMsg) {
        return <div className="page-container"><div style={{ padding: '20px', color: 'red' }}>{errorMsg}</div></div>;
    }

    return (
        <div className="page-container">
            {/* Header trang lịch */}
            <div className="page-header">
                <h2>{currentMonthYear || 'THÁNG 6, 2026'}</h2>
                <div className="nav-arrows-group">
                    <button className="nav-arrow-btn">&laquo;</button>
                    <button className="nav-arrow-btn">&raquo;</button>
                </div>
            </div>

            {/* Thẻ Card chứa dữ liệu lịch */}
            <div className="content-card dynamic-height">
                <div className="calendar-wrapper">

                    {/* Các thứ trong tuần */}
                    <div className="weekdays">
                        <span>MON</span>
                        <span>TUE</span>
                        <span>WED</span>
                        <span>THU</span>
                        <span>FRI</span>
                        <span>SAT</span>
                        <span>SUN</span>
                    </div>

                    {/* Ma trận các ô ngày */}
                    <div className="days-grid">
                        {!calendarDays && rows.map((rowNum) => (
                            <div className="calendar-row" key={`row-${rowNum}`}>
                                {daysInRow.map((dayNum) => {
                                    const isActive = rowNum === 1 && dayNum === 3;

                                    return (
                                        <div
                                            key={`day-${rowNum}-${dayNum}`}
                                            className={`day-cell ${isActive ? 'active-day' : ''}`}
                                        >
                                            {/* Số ngày */}
                                            <div className="day-number">
                                                <span>{dayNum}</span>
                                            </div>

                                            {isActive && exams.length > 0 && (
                                                <>
                                                    {/* Danh sách sự kiện tóm tắt ngoài ô lịch (Tối đa hiển thị 2 đề) */}
                                                    <div className="events">
                                                        {exams.slice(0, 2).map((exam) => (
                                                            <div
                                                                className="event"
                                                                key={exam.examId}
                                                                onClick={() => navigate(`/exams/${exam.examId}`)}
                                                                style={{ cursor: 'pointer' }}
                                                            >
                                                                {exam.examName}
                                                            </div>
                                                        ))}
                                                        {exams.length > 2 && (
                                                            <div className="more-event">+{exams.length - 2} khác</div>
                                                        )}
                                                    </div>

                                                    {/* Chi tiết toàn bộ sự kiện thật khi Hover chuột vào ô */}
                                                    <div className="event-popup">
                                                        {exams.map((exam) => (
                                                            <div key={`popup-${exam.examId}`} style={{ marginBottom: '4px', borderBottom: '1px solid #444', paddingBottom: '4px' }}>
                                                                <strong style={{ color: '#fff' }}>{exam.examName}</strong> {/* 🌟 Đổi từ exam.title -> exam.examName */}
                                                                <div style={{ fontSize: '11px', color: '#aaa', marginTop: '2px' }}>
                                                                    ⏱️ {exam.durationMinutes} phút | 📝 {exam.questionAmount || 0} câu {/* 🌟 Đổi sang durationMinutes và questionAmount */}
                                                                </div>
                                                            </div>
                                                        ))}
                                                    </div>
                                                </>
                                            )}
                                        </div>
                                    );
                                })}
                            </div>
                        ))}
                    </div>

                </div>
            </div>
        </div>
    );
};

export default Calendar;