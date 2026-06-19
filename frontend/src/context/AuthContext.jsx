import React, { createContext, useState, useContext } from 'react';

// 1. Khởi tạo Context
const AuthContext = createContext(null);

// 2. Tạo Provider Component (Dùng export để file khác import được)
export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(() => {
        // Đọc dữ liệu ban đầu từ localStorage nếu có sẵn
        const savedUser = localStorage.getItem("user");
        return savedUser ? JSON.parse(savedUser) : null;
    });

    // Hàm xử lý khi Đăng nhập thành công
    // Thay thế hàm login cũ trong AuthContext.js
    const login = (userData, callback) => {
        console.log("LOGIN CALLED", userData);

        localStorage.setItem("user", JSON.stringify(userData));

        console.log("AFTER SAVE:", localStorage.getItem("user"));

        setUser(userData);

        if (callback) callback();
    };

    // Hàm xử lý khi Đăng xuất
    const logout = () => {
        console.trace("LOGOUT CALLED");

        localStorage.removeItem("user");
        setUser(null);
    };

    return (
        <AuthContext.Provider value={{ user, login, logout, isAuthenticated: !!user }}>
            {children}
        </AuthContext.Provider>
    );
};

// 3. Custom hook để gọi Context nhanh gọn ở các trang con
export const useAuth = () => {
    return useContext(AuthContext);
};