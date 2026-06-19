import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext'; // 1. Import AuthContext
import '../css/Sidebar.css';

// ── SUB-COMPONENT: Menu Item ──
const MenuItem = ({ url, iconClass, text, active }) => {
    return (
        <a
            href={url}
            className={`menu-item ${active ? 'active' : ''}`}
        >
            <i className={iconClass}></i>
            <span>{text}</span>
        </a>
    );
};

// ── COMPONENT CHÍNH: Sidebar ──
const Sidebar = ({ menus = [], currentPath }) => {
    const navigate = useNavigate();
    const { user, logout } = useAuth(); // 2. Lấy thông tin user và hàm logout từ Context

    const renderRoleText = (role) => {
        switch (role) {
            case 'STUDENT': return 'Thí sinh';
            case 'TEACHER': return 'Giáo viên';
            default: return 'Quản trị viên';
        }
    };

    // 3. Hàm xử lý Đăng xuất đồng bộ với Backend
    const handleLogout = async () => {
        const isProd = import.meta.env.PROD;
        const BASE_URL = isProd ? 'https://exam-web-0jf4.onrender.com' : 'http://localhost:8080';

        try {
            // Gọi API báo cho Spring Security hủy Session
            await fetch(`${BASE_URL}/logout`, {
                method: 'POST',
                credentials: 'include'
            });
        } catch (error) {
            console.error("Lỗi khi gọi API logout:", error);
        } finally {
            // Dù API có lỗi hay không thì vẫn xóa sạch State ở Frontend và đẩy về trang Login
            logout();
            navigate('/login');
        }
    };

    return (
        <aside className="sidebar">
            <div className="sidebar-top">
                {/* Logo */}
                <div className="logo">
                    <img src="/images/logo.png" alt="Threads Logo" className="logo-img" />
                </div>

                {/* Menu Navigation */}
                <nav className="menu">
                    {menus.map((menu, index) => (
                        <MenuItem
                            key={index}
                            url={menu.url}
                            iconClass={menu.icon}
                            text={menu.text}
                            active={menu.url === currentPath}
                        />
                    ))}
                </nav>
            </div>

            {/* User Profile Bottom */}
            <div className="sidebar-bottom">
                <div className="user-profile">
                    <i className="far fa-user-circle avatar-icon"></i>

                    <div className="user-info">
                        {/* 4. Lấy dữ liệu tên và role thật từ Context tự động cập nhật */}
                        <h4>{user?.username || 'CHƯA ĐĂNG NHẬP'}</h4>
                        <span>{renderRoleText(user?.role)}</span>
                    </div>

                    {/* 5. Icon Logout gán sự kiện click */}
                    <i
                        className="fas fa-sign-out-alt logout-icon"
                        title="Đăng xuất khỏi hệ thống"
                        onClick={handleLogout}
                    />
                </div>
            </div>
        </aside>
    );
};

export default Sidebar;