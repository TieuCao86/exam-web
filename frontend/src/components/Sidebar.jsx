import React from 'react';
import '../css/Sidebar.css';

// ── SUB-COMPONENT: Tương đương với th:fragment="item" ngày xưa ──
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
const Sidebar = ({ menus = [], currentPath, userName, userRole }) => {

    const renderRoleText = (role) => {
        switch (role) {
            case 'STUDENT': return 'Thí sinh';
            case 'TEACHER': return 'Giáo viên';
            default: return 'Quản trị viên';
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
                            iconClass={menu.icon} // Bản cũ truyền icon qua biến menu.icon
                            text={menu.text}
                            active={menu.url === currentPath} // Tự động tính toán trạng thái active
                        />
                    ))}
                </nav>
            </div>

            {/* User Profile Bottom */}
            <div className="sidebar-bottom">
                <div className="user-profile">
                    <i className="far fa-user-circle avatar-icon"></i>

                    <div className="user-info">
                        <h4>{userName || 'TÊN NGƯỜI DÙNG'}</h4>
                        <span>{renderRoleText(userRole)}</span>
                    </div>

                    <i className="fas fa-cog settings-icon"></i>
                </div>
            </div>
        </aside>
    );
};

export default Sidebar;