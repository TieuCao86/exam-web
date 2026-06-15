import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, allowedRoles }) => {
    // Giả sử bạn lưu thông tin user sau khi login vào localStorage
    const user = JSON.parse(localStorage.getItem('user'));

    if (!user) {
        // Nếu chưa đăng nhập, đá về trang login
        return <Navigate to="/login" replace />;
    }

    if (allowedRoles && !allowedRoles.includes(user.role)) {
        // Nếu sai Role (ví dụ Học sinh đòi vào trang Giáo viên), đá về trang không có quyền
        return <Navigate to="/403" replace />;
    }

    // Nếu thỏa mãn hết thì cho đi tiếp vào trang con
    return children;
};

export default ProtectedRoute;