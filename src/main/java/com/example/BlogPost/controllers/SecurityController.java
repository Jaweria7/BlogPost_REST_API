package com.example.BlogPost.controllers;

import com.example.BlogPost.entities.User;
import com.example.BlogPost.repositories.UserRepository;
import com.example.BlogPost.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SecurityController {

    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public SecurityController(PasswordEncoder passwordEncoder, UserService userService) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user, @RequestParam String confirmPassword) {
        if (userService.usernameExists(user.getUsername())) {
            return ResponseEntity.badRequest().body("Username already exists.");
        }
        if (!user.getPassword().equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_AUTHOR");
        userService.registerNewUser(user);

        return ResponseEntity.ok("User registered successfully.");
    }
}