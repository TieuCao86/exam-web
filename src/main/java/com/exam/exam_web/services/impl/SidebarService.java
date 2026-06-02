package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.MenuItem;
import com.exam.exam_web.entity.Role;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SidebarService {

    private final Map<Role, List<MenuItem>> roleMenus = Map.of(

            Role.STUDENT,
            List.of(
                    new MenuItem(
                            "/calendar",
                            "far fa-calendar-alt",
                            "LỊCH LÀM BÀI"
                    ),
                    new MenuItem(
                            "/courses",
                            "fas fa-book-open",
                            "MÔN HỌC"
                    ),
                    new MenuItem(
                            "/exams",
                            "far fa-edit",
                            "KÌ THI"
                    ),
                    new MenuItem(
                            "/history",
                            "fas fa-history",
                            "LỊCH SỬ LÀM BÀI"
                    )
            ),

            Role.TEACHER,
            List.of(
                    new MenuItem(
                            "/teacher/dashboard",
                            "fas fa-chart-line",
                            "THỐNG KÊ"
                    ),
                    new MenuItem(
                            "/teacher/question-bank",
                            "fas fa-database",
                            "NGÂN HÀNG ĐỀ THI"
                    )
            ),

            Role.ADMIN,
            List.of(
                    new MenuItem(
                            "/admin/users",
                            "fas fa-users",
                            "QUẢN LÝ USER"
                    ),
                    new MenuItem(
                            "/admin/settings",
                            "fas fa-cogs",
                            "CẤU HÌNH"
                    )
            )
    );

    public List<MenuItem> getMenus(Role role) {
        return roleMenus.getOrDefault(role, List.of());
    }
}
