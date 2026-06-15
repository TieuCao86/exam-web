import { Routes, Route, Navigate } from "react-router-dom";
import MainLayout from './components/MainLayout';
import Calendar from './pages/Calendar';
import CoursePage from './pages/CoursePage';

function App() {

    const mockMenus = [
        { url: '/calendar', icon: 'fas fa-calendar-alt', text: 'Lịch học' },
        { url: '/courses', icon: 'fas fa-book', text: 'Khóa học' },
    ];

    return (
        <Routes>

            <Route
                path="/"
                element={<Navigate to="/calendar" replace />}
            />

            <Route
                path="/calendar"
                element={
                    <MainLayout menus={mockMenus} currentPath="/calendar">
                        <Calendar />
                    </MainLayout>
                }
            />

            <Route
                path="/courses"
                element={
                    <MainLayout menus={mockMenus} currentPath="/courses">
                        <CoursePage />
                    </MainLayout>
                }
            />

        </Routes>
    );
}

export default App;