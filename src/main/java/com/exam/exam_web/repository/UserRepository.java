package com.exam.exam_web.repository;

import com.exam.exam_web.entity.Role;
import com.exam.exam_web.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByAccount_AccountId(String accountId);

    Optional<User> findByAccount_Username(String username);

    List<User> findByAccount_Role(Role role);

    List<User> findByAccount_ActiveTrue();

    @EntityGraph(attributePaths = {"subjects"})
    Optional<User> findWithSubjectsByUserId(String userId);
}