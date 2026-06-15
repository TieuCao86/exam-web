import React from 'react';
import Sidebar from './Sidebar';
import '../css/AppLayout.css';

// 1. Cập nhật để nhận đầy đủ các props (menus, currentPath, userName, userRole) được truyền từ App.jsx xuống
const MainLayout = ({ children, menus, currentPath, userName, userRole }) => {
    return (
        <div className="app-container">
            {/* 2. Truyền bắc cầu các props này xuống cho component Sidebar */}
            <Sidebar
                menus={menus}
                currentPath={currentPath}
                userName={userName}
                userRole={userRole}
            />

            {/* Khung chứa nội dung động bên phải */}
            <main className="main-content-wrapper">
                {children}
            </main>
        </div>
    );
};

export default MainLayout;