import { Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "./components/MainLayout";
import ProtectedRoute from "./components/ProtectedRoute";

import Login from "./pages/Login";
import Calendar from "./pages/Calendar";
import CoursePage from "./pages/CoursePage";
import CourseDetailPage from "./pages/CourseDetailPage.jsx";
import ExamListPage from "./pages/ExamListPage.jsx";
import ExamDetailPage from "./pages/ExamDetailPage.jsx";
import TakeExamPage from "./pages/TakeExamPage.jsx";

function App() {

    const mockMenus = [
        { url: "/calendar", icon: "fas fa-calendar-alt", text: "Lịch học" },
        { url: "/courses", icon: "fas fa-book", text: "Khóa học" },
        { url: "/exams", icon: "fas fa-file-alt", text: "Bài kiểm tra" }
    ];

    return (
        <Routes>

            <Route path="/login" element={<Login />} />

            <Route
                path="/"
                element={<Navigate to="/calendar" replace />}
            />

            <Route
                path="/calendar"
                element={
                    <ProtectedRoute allowedRoles={["STUDENT"]}>
                        <MainLayout menus={mockMenus} currentPath="/calendar">
                            <Calendar />
                        </MainLayout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/courses"
                element={
                    <ProtectedRoute allowedRoles={["STUDENT"]}>
                        <MainLayout menus={mockMenus} currentPath="/courses">
                            <CoursePage />
                        </MainLayout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/courses/:courseId"
                element={
                    <ProtectedRoute allowedRoles={["STUDENT"]}>
                        <MainLayout menus={mockMenus}>
                            <CourseDetailPage />
                        </MainLayout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/exams"
                element={
                    <ProtectedRoute allowedRoles={["STUDENT"]}>
                        <MainLayout menus={mockMenus} currentPath="/exams">
                            <ExamListPage />
                        </MainLayout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/exams/:examId"
                element = {
                    <ProtectedRoute allowedRoles={["STUDENT"]}>
                        <MainLayout menus={mockMenus} currentPath="/exams">
                            <ExamDetailPage />
                        </MainLayout>
                    </ProtectedRoute>
                }
            />

            <Route
                path="/exams/:examId/start"
                element={
                    <ProtectedRoute allowedRoles={["STUDENT"]}>
                        <MainLayout menus={mockMenus} currentPath="/exams">
                            <TakeExamPage />
                        </MainLayout>
                    </ProtectedRoute>
                }
            />

        </Routes>
    );
}

export default App;