// src/utils/api.js
export const secureFetch = async (url, options = {}, logoutCallback, navigateCallback) => {
    const defaultOptions = {
        credentials: 'include',
        ...options,
    };

    try {
        const response = await fetch(url, defaultOptions);

        // BẮT TRÚNG THỦ PHẠM RESTART SERVER:
        if (response.status === 401 || response.status === 403) {
            console.warn("Phiên đăng nhập hết hạn do máy chủ khởi động lại!");
            if (logoutCallback) logoutCallback(); // Xóa sạch localStorage qua AuthContext
            if (navigateCallback) navigateCallback('/login'); // Đá về trang login
            throw new Error("Session expired");
        }

        return response;
    } catch (error) {
        throw error;
    }
};