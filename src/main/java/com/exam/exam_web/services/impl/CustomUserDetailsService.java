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
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        Account account = accountRepository
                .findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found: " + username
                        ));

        if (!account.isActive()) {
            throw new DisabledException("Account disabled");
        }

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPasswordHash(),
                List.of(
                        new SimpleGrantedAuthority(
                                "ROLE_" + account.getRole().name()
                        )
                )
        );
    }
}