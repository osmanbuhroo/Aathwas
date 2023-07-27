package com.Aathwas.controller;

import com.Aathwas.model.User;
import com.Aathwas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class RegistrationController {
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/registration")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        // Check if the user already exists in the database
        if (userRepository.existsByPhoneNumber(user.getPhoneNumber())) {
            return ResponseEntity.badRequest().body("User with this mobile number already exists");
        }

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
