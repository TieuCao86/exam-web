import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, allowedRoles }) => {
    const { user, isAuthenticated } = useAuth();

    console.log("PROTECTED ROUTE CHECKING. User:", user);

    // 1. Kiểm tra đăng nhập
    if (!isAuthenticated) {
        return <Navigate to="/login" replace />;
    }

    // 2. Kiểm tra phân quyền
    if (allowedRoles && !allowedRoles.includes(user?.role)) {
        return <Navigate to="/403" replace />;
    }

    return children;
};

export default ProtectedRoute;