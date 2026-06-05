package com.exam.exam_web.config.interceptor;

import com.exam.exam_web.entity.Role;
import com.exam.exam_web.services.impl.SidebarService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class SidebarInterceptor implements HandlerInterceptor {

    private final SidebarService sidebarService;

    public SidebarInterceptor(SidebarService sidebarService) {
        this.sidebarService = sidebarService;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {

        if (modelAndView == null) return;

        Role role = Role.STUDENT;

        modelAndView.addObject("menus", sidebarService.getMenus(role));
        modelAndView.addObject("userRole", role.name());
        modelAndView.addObject("userName", "NGUYỄN BÁ VIỆT");

        String currentPath = request.getRequestURI();

        if (currentPath.startsWith("/courses")) {
            currentPath = "/courses";
        } else if (currentPath.startsWith("/exams")) {
            currentPath = "/exams";
        } else if (currentPath.startsWith("/calendar")) {
            currentPath = "/calendar";
        }

        modelAndView.addObject("currentPath", currentPath);
    }
}
