package com.exam.exam_web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        String redirect = switch (role) {
            case "ROLE_STUDENT" -> "/calendar";
            case "ROLE_TEACHER" -> "/questions";
            case "ROLE_ADMIN"   -> "/students";
            default             -> "/login";
        };
        res.sendRedirect(redirect);
    }
}
