import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../css/Login.css';

export default function Login() {
    const navigate = useNavigate();
    const { login } = useAuth();
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [errorMsg, setErrorMsg] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        console.log("ĐÃ CLICK NÚT LOGIN THÀNH CÔNG! Đang chuẩn bị gửi dữ liệu...");
        setLoading(true);
        setErrorMsg('');

        const formData = new FormData();
        formData.append('username', username);
        formData.append('password', password);

        const isProd = import.meta.env.PROD;
        const targetUrl = isProd
            ? 'https://exam-web-0jf4.onrender.com/login'
            : 'http://localhost:8080/login';

        try {
            const response = await fetch(targetUrl, {
                method: 'POST',
                credentials: 'include',
                headers: {
                    'Accept': 'application/json',
                    'X-Requested-With': 'XMLHttpRequest'
                },
                body: formData
            });

            console.log("Mã trạng thái phản hồi từ Server:", response.status);

            if (response.status === 200 || response.ok) {
                const userData = await response.json();

                login(userData);

                navigate("/calendar");
            } else if (response.status === 401) {
                setErrorMsg('Tài khoản hoặc mật khẩu không chính xác.');
            } else {
                setErrorMsg(`Đăng nhập thất bại (Mã lỗi: ${response.status}). Vui lòng thử lại.`);
            }
        } catch (error) {
            console.error("Lỗi kết nối nghiêm trọng:", error);
            setErrorMsg('Không thể kết nối đến máy chủ.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="login-layout">
            <div className="login-left-panel">
                <div className="login-logo-wrap">
                    <img src="/images/logo.png" alt="Exam System Logo" />
                </div>
            </div>

            <div className="login-right-panel">
                <h1 className="login-title">LOGIN</h1>

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