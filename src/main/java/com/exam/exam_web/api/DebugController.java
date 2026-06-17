package com.exam.exam_web.api;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DebugController {

    @GetMapping("/api/debug")
    public String debug(
            Authentication authentication,
            HttpSession session) {

        return "session=" + session.getId()
                + " auth=" + authentication;
    }

    @GetMapping("/api/debug-role")
    public Object debug(Authentication auth) {
        return auth.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
