package com.shivam.urlshortner.service;

import com.shivam.urlshortner.entity.User;
import com.shivam.urlshortner.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(String username, String password) {

        // simple validation
        if (username == null || username.isEmpty()) {
            throw new RuntimeException("Username cannot be empty");
        }

        if (password == null || password.isEmpty()) {
            throw new RuntimeException("Password cannot be empty");
        }

        // check if user exists
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("USER");

        return userRepository.save(user);
    }
    public User loginUser(String username, String password) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // compare password
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }
    public User registerAdmin(String username, String password) {

        User user = new User();
        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(password)); // 🔥 correct hash
        user.setRole("ADMIN");

        return userRepository.save(user);
    }
}