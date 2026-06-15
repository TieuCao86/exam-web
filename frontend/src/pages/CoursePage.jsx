import { useEffect, useState } from "react";
import CourseList from "../components/CourseList";

const API_BASE = import.meta.env.PROD
    ? "https://exam-web-0jf4.onrender.com"
    : "http://localhost:8080";

export default function CoursePage() {

    const [courses, setCourses] = useState([]);
    const [currentPage, setCurrentPage] = useState(0);
    const [hasNext, setHasNext] = useState(false);

    const loadCourses = async (page = 0) => {

        try {

            const response = await fetch(
                `${API_BASE}/api/student/courses/page?userId=STUDENT001&page=${page}`
            );

            const data = await response.json();

            setCourses(data.content);
            setCurrentPage(data.currentPage);
            setHasNext(data.hasNext);

        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        loadCourses(0);
    }, []);

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