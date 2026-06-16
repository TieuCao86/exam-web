package com.exam.exam_web.services;

import com.exam.exam_web.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Account account;

    public String getId() {
        return String.valueOf(this.account.getAccountId());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Tự động thêm tiền tố ROLE_ để khớp với hasRole/hasAnyRole trong SecurityConfig
        return List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()));
    }

    @Override
    public String getPassword() {
        return account.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return account.getUsername();
    }

    @Override
    public boolean isEnabled() {
        return account.isActive();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }
}
