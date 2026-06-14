import React, { useState } from 'react';
import './Login.css';

export default function Login() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setErrorMsg('');

        // Đóng gói dạng FormData tương thích cấu hình FormLogin của Spring Security
        const formData = new FormData();
        formData.append('username', username);
        formData.append('password', password);

        // TỰ ĐỘNG ĐỊNH TUYẾN: Nếu chạy production (Vercel) thì trỏ thẳng về Render, local thì dùng Proxy
        const isProd = import.meta.env.PROD;
        const targetUrl = isProd
            ? 'https://exam-web-0jf4.onrender.com/login'
            : '/login';

        try {
            const response = await fetch(targetUrl, {
                method: 'POST',
                headers: {
                    // ĐỒNG BỘ: Ép cả 2 nhận diện để Spring Boot không thể bắt hụt tín hiệu Ajax
                    'Accept': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData,
            });

            // In trực tiếp trạng thái ra tab Console (F12) để bạn dễ kiểm tra kiểm toán
            console.log("Mã trạng thái phản hồi từ Server:", response.status);

            if (response.status === 200 || response.ok) {
                alert('Đăng nhập bằng React thành công rực rỡ!');
                // window.location.href = '/dashboard';
            } else if (response.status === 401) {
                setErrorMsg('Tài khoản hoặc mật khẩu không chính xác.');
            } else {
                // Nếu in ra mã 302 hoặc mã khác, dòng dưới đây sẽ bắt được
                setErrorMsg(`Đăng nhập thất bại (Mã lỗi: ${response.status}). Vui lòng thử lại.`);
            }
        } catch (error) {
            console.error("Lỗi kết nối nghiêm trọng:", error);
            setErrorMsg('Không thể kết nối đến máy chủ.');
        } finally {
            setLoading(false); // 💡 Tắt trạng thái loading để có thể bấm lại nếu sai mật khẩu
        }
    };

    return (
        <div className="login-layout">

            {/* TRÁI: Khung Logo thương hiệu */}
            <div className="login-left-panel">
                <div className="login-logo-wrap">
                    <img src="/images/logo.png" alt="Exam System Logo" />
                </div>
            </div>

            {/* PHẢI: Khung nhập liệu Form */}
            <div className="login-right-panel">
                <h1 className="login-title">LOGIN</h1>

                {/* Cảnh báo lỗi thông minh */}
                {errorMsg && (
                    <div className="login-error-label">
                        {errorMsg}
                    </div>
                )}

                <form onSubmit={handleSubmit} className="login-form">
                    <div className="login-field-group">
                        <input
                            type="text"
                            placeholder="Username"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                            autoComplete="username"
                        />
                    </div>

                    <div className="login-field-group">
                        <input
                            type="password"
                            placeholder="Password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            autoComplete="current-password"
                        />
                    </div>

                    <button
                        type="submit"
                        disabled={loading}
                        className="login-submit-btn"
                    >
                        {loading ? 'Logging in...' : 'Login'}
                    </button>
                </form>
            </div>

        </div>
    );
}