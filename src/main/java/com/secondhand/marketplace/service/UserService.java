package com.secondhand.marketplace.service;

import com.secondhand.marketplace.entity.User;
import com.secondhand.marketplace.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        log.info("Registering user: {}", user);
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("User with username " + user.getUsername() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User savedUser = userRepository.save(user);

        log.info("User registered successfully with ID: {}", savedUser.getId());
        return savedUser;
    }
}
