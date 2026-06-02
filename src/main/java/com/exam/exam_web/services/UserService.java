package com.exam.exam_web.services;

import com.exam.exam_web.dto.UserDTO;
import com.exam.exam_web.entity.Role;

import java.util.List;

public interface UserService {

    UserDTO createStudent(
            UserDTO userDTO
    );

    UserDTO createTeacher(
            UserDTO userDTO
    );

    UserDTO findById(
            String userId
    );

    UserDTO findByAccountId(
            String accountId
    );

    UserDTO findByUsername(
            String username
    );

    List<UserDTO> findAll();

    List<UserDTO> findByRole(
            Role role
    );

    List<UserDTO> findStudents();

    List<UserDTO> findTeachers();

    UserDTO update(
            UserDTO userDTO
    );

    void delete(
            String userId
    );

    void lockAccount(
            String userId
    );

    void unlockAccount(
            String userId
    );

    UserDTO login(
            String username,
            String password
    );

    void changePassword(
            String userId,
            String oldPassword,
            String newPassword
    );

    UserDTO updateMyProfile(
            UserDTO userDTO
    );

    void assignCourse(
            String teacherId,
            String courseId
    );

    void removeCourse(
            String courseId
    );
}