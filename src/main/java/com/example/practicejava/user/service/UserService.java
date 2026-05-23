package com.example.practicejava.user.service;

import com.example.practicejava.user.User;
import com.example.practicejava.user.UserNotFoundException;
import com.example.practicejava.user.dto.UpdateUserRequest;
import com.example.practicejava.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public User findMe(UUID userId) {
        return findById(userId);
    }

    public User updateMe(UUID userId, UpdateUserRequest request) {
        User user = findById(userId);
        user.setDisplayName(request.displayName());
        user.setAvatarUrl(request.avatarUrl());
        return user;
    }

    public User update(UUID id, UpdateUserRequest request) {
        User user = findById(id);
        user.setDisplayName(request.displayName());
        user.setAvatarUrl(request.avatarUrl());
        return user;
    }

    public void delete(UUID id, UUID deletedBy) {
        User user = findById(id);
        user.softDelete(deletedBy);
    }
}
