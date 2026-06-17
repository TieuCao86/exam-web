import { Routes, Route, Navigate } from "react-router-dom";
import MainLayout from "./components/MainLayout";
import ProtectedRoute from "./components/ProtectedRoute";

import Login from "./pages/Login";
import Calendar from "./pages/Calendar";
import CoursePage from "./pages/CoursePage";
import CourseDetailPage from "./pages/CourseDetailPage.jsx";

function App() {

    const mockMenus = [
        { url: "/calendar", icon: "fas fa-calendar-alt", text: "Lịch học" },
        { url: "/courses", icon: "fas fa-book", text: "Khóa học" }
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

        </Routes>
    );
}

export default App;