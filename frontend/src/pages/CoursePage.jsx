import { useEffect, useState } from "react";
import CourseList from "../components/CourseList";
import { useAuth } from "../context/AuthContext.jsx";
import { useNavigate } from "react-router-dom"; // Import thêm useNavigate để điều hướng

const API_BASE = import.meta.env.PROD
    ? "https://exam-web-0jf4.onrender.com"
    : "http://localhost:8080";

export default function CoursePage() {
    const navigate = useNavigate();
    const [courses, setCourses] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [hasNext, setHasNext] = useState(false);

    const { user, logout } = useAuth(); // Lấy thêm hàm logout từ Context

    const loadCourses = async (page = 0) => {
        if (!user || !user.id) return;

        try {
            const response = await fetch(
                `${API_BASE}/api/student/courses/page?userId=${user.id}&page=${page}`,
                {
                    method: 'GET',
                    credentials: 'include',
                    headers: {
                        'Accept': 'application/json',
                        'X-Requested-With': 'XMLHttpRequest'
                    }
                }
            );

            // CHẶN ĐỨT LỖI TREO GIAO DIỆN KHI RESTART SERVER BACKEND:
            if (response.status === 401 || response.status === 403) {
                console.warn("Phiên đăng nhập hết hạn do máy chủ khởi động lại. Tự động chuyển hướng...");
                logout(); // Xóa sạch thông tin user cũ trong localStorage
                navigate('/login'); // Ép quay lại màn hình đăng nhập
                return;
            }

            if (!response.ok) {
                throw new Error(`Server returned status ${response.status}`);
            }

            const data = await response.json();

            setCourses(data.content || []);
            setCurrentPage(data.pageNumber);
            setHasNext(!data.last);
        } catch (error) {
            console.error("Lỗi khi tải danh sách khóa học:", error);
        }
    };

    useEffect(() => {
        loadCourses(0);
    }, [user]); // Bổ sung user vào dependency array để chạy đúng khi thông tin user sẵn sàng

    const handlePageChange = (page) => {
        loadCourses(page);
    };

    return (
        <CourseList
            courses={courses}
            semesterOptions={[
                { value: "HK1", label: "Học kỳ 1" },
                { value: "HK2", label: "Học kỳ 2" }
            ]}
            yearOptions={[
                { value: "2025-2026", label: "2025-2026" }
            ]}
            currentPage={currentPage}
            hasNext={hasNext}
            onPageChange={handlePageChange}
        />
    );
}