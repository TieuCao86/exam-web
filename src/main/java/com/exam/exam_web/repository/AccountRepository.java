package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Account;
import com.exam.exam_web.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByUsername(String username);

    Optional<Account> findByEmail(String email);

    List<Account> findByRole(Role role);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}