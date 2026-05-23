package com.example.practicejava.user;

import com.example.practicejava.user.dto.CreateUserRequest;
import com.example.practicejava.user.dto.UpdateUserRequest;
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
    public User findById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User create(CreateUserRequest request) {
        return userRepository.save(new User(request.name(), request.email()));
    }

    public User update(UUID id, UpdateUserRequest request) {
        User user = findById(id);
        user.setName(request.name());
        user.setEmail(request.email());
        return user;
    }

    public void delete(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
}
