package com.exam.exam_web.services.impl;

import com.exam.exam_web.entity.Account;
import com.exam.exam_web.repository.AccountRepository;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AccountRepository accountRepository;

    public CustomUserDetailsService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));

        // Dùng .roles() và truyền chuỗi "TEACHER" / "STUDENT" từ Enum .name()
        // Spring Security sẽ tự hiểu và map khớp với .hasAnyRole("TEACHER", "ADMIN") trong SecurityConfig của bạn.
        return org.springframework.security.core.userdetails.User.builder()
                .username(account.getUsername())
                .password(account.getPasswordHash()) // Đảm bảo cột password_hash dưới DB lưu đủ 60 ký tự chuỗi băm
                .roles(account.getRole().name())     // Ép chuỗi Enum chuẩn (TEACHER, STUDENT, ADMIN)
                .disabled(!account.isActive())
                .build();
    }
}