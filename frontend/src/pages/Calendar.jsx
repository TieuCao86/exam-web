import React from 'react';
import '../css/Calendar.css'; // File CSS riêng cho Lịch bên dưới

const Calendar = ({ currentMonthYear, calendarDays }) => {
    // Tạo mảng giả lập 5 hàng x 7 cột giống hệt logic Thymeleaf cũ khi calendarDays == null
    const rows = Array.from({ length: 5 }, (_, rowIndex) => rowIndex + 1);
    const daysInRow = Array.from({ length: 7 }, (_, dayIndex) => dayIndex + 1);

    return (
        <div className="page-container">
            {/* Header trang lịch */}
            <div className="page-header">
                <h2>{currentMonthYear || 'THÁNG 1, 2026'}</h2>
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
                                    // Giữ nguyên logic cũ: Hàng 1, Cột 3 sẽ là active-day
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

                                            {/* Danh sách sự kiện tóm tắt */}
                                            <div className="events">
                                                <div className="event">Java</div>
                                                <div className="event">CSDL</div>
                                                <div className="event">Mạng</div>
                                                <div className="more-event">+2 khác</div>
                                            </div>

                                            {/* Chi tiết sự kiện khi Hover vào ô */}
                                            <div className="event-popup">
                                                <div>Java giữa kỳ - 08:00</div>
                                                <div>CSDL - 10:00</div>
                                                <div>Mạng - 13:00</div>
                                                <div>ATTT - 15:00</div>
                                                <div>Phân tán - 18:00</div>
                                            </div>
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