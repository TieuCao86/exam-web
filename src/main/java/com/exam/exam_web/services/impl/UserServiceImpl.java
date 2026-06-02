package com.exam.exam_web.services.impl;

import com.exam.exam_web.dto.UserDTO;
import com.exam.exam_web.entity.*;
import com.exam.exam_web.mapper.UserMapper;
import com.exam.exam_web.repository.AccountRepository;
import com.exam.exam_web.repository.CourseRepository;
import com.exam.exam_web.repository.SubjectRepository;
import com.exam.exam_web.repository.UserRepository;
import com.exam.exam_web.services.UserService;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final CourseRepository courseRepository;
    private final AccountRepository accountRepository;

    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            SubjectRepository subjectRepository,
            CourseRepository courseRepository, AccountRepository accountRepository,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.courseRepository = courseRepository;
        this.accountRepository = accountRepository;
        this.userMapper = userMapper;
    }

    // ================= AUTH (TEMP) =================

    private UserDTO getCurrentUser() {
        // TODO: replace with Spring Security
        return null;
    }

    private void requireAdmin() {
        UserDTO current = getCurrentUser();

        if (current == null
                || current.getAccount() == null
                || current.getAccount().getRole() != Role.ADMIN) {
            throw new RuntimeException("Access denied. Admin only.");
        }
    }

    // ================= CREATE USER =================

    @Override
    public UserDTO createStudent(UserDTO userDTO) {
        return createUser(userDTO, Role.STUDENT);
    }

    @Override
    public UserDTO createTeacher(UserDTO userDTO) {
        return createUser(userDTO, Role.TEACHER);
    }

    private UserDTO createUser(UserDTO userDTO, Role role) {
        requireAdmin();

        User user = userMapper.toEntity(userDTO);
        user.setAccount(createDefaultAccount(user, role));

        return userMapper.toDTO(userRepository.save(user));
    }

    private Account createDefaultAccount(User user, Role role) {

        String baseUsername = generateUsername(user.getFullName());
        String username = baseUsername;
        int count = 1;

        while (userRepository.findByAccount_Username(username).isPresent()) {
            username = baseUsername + count;
            count++;
        }

        return Account.builder()
                .username(username)
                .email(username + "@school.com")
                .passwordHash("1111")
                .role(role)
                .active(true)
                .build();
    }

    // ================= QUERY =================

    @Override
    public UserDTO findById(String userId) {
        return userRepository.findWithSubjectsByUserId(userId)
                .map(userMapper::toDTO)
                .orElse(null);
    }

    @Override
    public UserDTO findByAccountId(String accountId) {
        return userRepository.findByAccount_AccountId(accountId)
                .map(userMapper::toDTO)
                .orElse(null);
    }

    @Override
    public UserDTO findByUsername(String username) {
        return userRepository.findByAccount_Username(username)
                .map(userMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<UserDTO> findAll() {
        requireAdmin();

        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public List<UserDTO> findByRole(Role role) {
        requireAdmin();

        return userRepository.findByAccount_Role(role)
                .stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    public List<UserDTO> findStudents() {
        return findByRole(Role.STUDENT);
    }

    @Override
    public List<UserDTO> findTeachers() {
        return findByRole(Role.TEACHER);
    }

    // ================= UPDATE =================

    @Override
    public UserDTO update(UserDTO userDTO) {
        requireAdmin();

        User updated = userRepository.save(
                userMapper.toEntity(userDTO)
        );

        return userMapper.toDTO(updated);
    }

    @Override
    public void delete(String userId) {
        requireAdmin();

        if (!userRepository.existsById(userId)) return;

        userRepository.deleteById(userId);
    }

    @Override
    public void lockAccount(String userId) {
        requireAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getAccount().setActive(false);
        userRepository.save(user);
    }

    @Override
    public void unlockAccount(String userId) {
        requireAdmin();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.getAccount().setActive(true);
        userRepository.save(user);
    }

    @Override
    public UserDTO login(String username, String password) {
        return userRepository.findByAccount_AccountId(username)
                .filter(u -> u.getAccount().getPasswordHash().equals(password))
                .map(userMapper::toDTO)
                .orElse(null);
    }

    @Override
    public void changePassword(String userId, String oldPassword, String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getAccount().getPasswordHash().equals(oldPassword)) {
            throw new RuntimeException("Old password incorrect");
        }

        user.getAccount().setPasswordHash(newPassword);
        userRepository.save(user);
    }

    @Override
    public UserDTO updateMyProfile(UserDTO userDTO) {
        User updated = userRepository.save(
                userMapper.toEntity(userDTO)
        );

        return userMapper.toDTO(updated);
    }

    // ================= BUSINESS =================

    @Override
    public void assignCourse(String teacherId, String courseId) {

        requireAdmin();

        Account teacher = accountRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new RuntimeException("User is not a teacher");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTeacher(teacher);

        courseRepository.save(course);
    }

    @Override
    public void removeCourse(String courseId) {
        requireAdmin();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTeacher(null);
        courseRepository.save(course);
    }

    // ================= UTILS =================

    private String generateUsername(String fullName) {

        String normalized = Normalizer.normalize(fullName, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{M}", "")
                .replaceAll("đ", "d")
                .replaceAll("Đ", "D")
                .replaceAll("\\s+", "")
                .toLowerCase();
    }
}