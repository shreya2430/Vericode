package com.vericode.controller;

import com.vericode.model.User;
import com.vericode.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // POST /api/users/register
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");

        // Validate required fields
        if (username == null || username.isBlank() ||
                name == null || name.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "All fields are required: username, name, email, password"));
        }

        // Check if username or email already exists
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Username already taken"));
        }
        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email already registered"));
        }

        // Hash password and save
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, name, email, hashedPassword);
        User saved = userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", saved.getId(),
                "username", saved.getUsername(),
                "name", saved.getName(),
                "email", saved.getEmail()
        ));
    }

    // POST /api/users/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("username");
        String password = request.get("password");

        if (username == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Username and password are required"));
        }

        return userRepository.findByUsername(username)
                .map(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return ResponseEntity.ok(Map.of(
                                "id", user.getId(),
                                "username", user.getUsername(),
                                "name", user.getName(),
                                "email", user.getEmail(),
                                "message", "Login successful"
                        ));
                    }
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body((Map<String, Object>) Map.<String, Object>of("error", "Invalid password"));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found")));
    }

    // GET /api/users - Get all users (for reviewer dropdown)
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(
                userRepository.findAll().stream()
                        .map(user -> Map.of(
                                "id", user.getId(),
                                "username", user.getUsername(),
                                "name", user.getName(),
                                "email", user.getEmail()
                        ))
                        .toList()
        );
    }
}