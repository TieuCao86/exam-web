import React, { useState } from 'react';
import '../css/CourseList.css'; // File CSS riêng của trang khóa học bên dưới

const CourseList = ({
                        courses = [],
                        semesterOptions = [],
                        yearOptions = [],
                        keyword: initialKeyword = '',
                        semester: initialSemester = '',
                        year: initialYear = '',
                        currentPage = 0,
                        hasNext = false,
                        onFilterSubmit, // Hàm callback truyền từ App.jsx lên khi bấm tìm kiếm
                        onPageChange    // Hàm callback khi bấm chuyển trang pagination
                    }) => {
    // Quản lý trạng thái các ô nhập liệu bằng React State
    const [keyword, setKeyword] = useState(initialKeyword);
    const [semester, setSemester] = useState(initialSemester);
    const [year, setYear] = useState(initialYear);

    const handleSubmit = (e) => {
        e.preventDefault();
        if (onFilterSubmit) {
            onFilterSubmit({ keyword, semester, year });
        }
    };

    return (
        <div className="page-container">
            {/* Tiêu đề trang */}
            <div className="page-header">
                <h2>CÁC KHÓA HỌC CỦA TÔI</h2>
            </div>

            {/* Thanh Action Bar chứa Form bộ lọc */}
            <div className="action-bar">
                <form onSubmit={handleSubmit} className="course-filter-form">
                    {/* Search Input */}
                    <input
                        type="text"
                        placeholder="Tìm kiếm khóa học..."
                        value={keyword}
                        onChange={(e) => setKeyword(e.target.value)}
                    />

                    {/* Học kỳ Select */}
                    <select
                        value={semester}
                        onChange={(e) => setSemester(e.target.value)}
                        className="filter-select"
                    >
                        <option value="">Tất cả học kỳ</option>
                        {semesterOptions.map((opt, index) => (
                            <option key={`sem-${index}`} value={opt.value}>
                                {opt.label}
                            </option>
                        ))}
                    </select>

                    {/* Năm học Select */}
                    <select
                        value={year}
                        onChange={(e) => setYear(e.target.value)}
                        className="filter-select"
                    >
                        <option value="">Tất cả năm</option>
                        {yearOptions.map((opt, index) => (
                            <option key={`year-${index}`} value={opt.value}>
                                {opt.label}
                            </option>
                        ))}
                    </select>

                    <button type="submit" className="btn-primary-blue">
                        Tìm kiếm
                    </button>
                </form>
            </div>

            {/* Content Card chứa lưới danh sách khóa học */}
            <div className="content-card">
                <div className="course-grid">
                    {courses.map((c) => (
                        <a
                            key={c.courseId}
                            href={`/courses/${c.courseId}`}
                            className="course-link"
                        >
                            <div className="course-card">
                                {/* Ảnh bìa khóa học */}
                                <div className="course-header">
                                    <img src={c.imageUrl || "/images/default-course.png"} alt="course image" />
                                </div>

                                {/* Thông tin khóa học */}
                                <div className="course-body">
                                    <h3>{c.courseName}</h3>

                                    <p className="course-info-meta">
                                        <i className="fas fa-calendar"></i>{' '}
                                        <span>{c.academicYear}</span> - <span>{c.semester}</span>
                                    </p>

                                    <div className="progress-box">
                                        <div className="progress-bar">
                                            {/* Gán chiều rộng style động trong React */}
                                            <div
                                                className="progress-fill"
                                                style={{ width: `${c.progress}%` }}
                                            ></div>
                                        </div>
                                        <small>{c.progress}% hoàn thành</small>
                                    </div>
                                </div>
                            </div>
                        </a>
                    ))}
                </div>
            </div>

            {/* Điều hướng Phân trang (Pagination) */}
            <div className="pagination-wrapper">
                <div className="pagination">
                    {currentPage > 0 && (
                        <button onClick={() => onPageChange && onPageChange(currentPage - 1)} className="nav-arrow-btn">
                            &laquo;
                        </button>
                    )}
                    <span className="current-page-num">{currentPage + 1}</span>
                    {hasNext && (
                        <button onClick={() => onPageChange && onPageChange(currentPage + 1)} className="nav-arrow-btn">
                            &raquo;
                        </button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default CourseList;