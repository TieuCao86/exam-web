import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const ProtectedRoute = ({ children, allowedRoles }) => {
    const { user } = useAuth();

    console.log("STATE USER:", user);
    console.log("LOCAL STORAGE:", localStorage.getItem("user"));

    const activeUser =
        user ||
        JSON.parse(localStorage.getItem("user"));

    console.log("ACTIVE USER:", activeUser);

    if (!activeUser) {
        return <Navigate to="/login" replace />;
    }

    if (
        allowedRoles &&
        !allowedRoles.includes(activeUser.role)
    ) {
        return <Navigate to="/403" replace />;
    }

    return children;
};

export default ProtectedRoute;