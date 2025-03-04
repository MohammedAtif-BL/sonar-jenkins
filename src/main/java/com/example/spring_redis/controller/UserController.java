package com.example.spring_redis.controller;

import com.example.spring_redis.entity.User;
import com.example.spring_redis.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return users.isEmpty() ? ResponseEntity.ok(Collections.emptyList()) : ResponseEntity.ok(users);
    }


    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // Returns 404 if user not found
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user.getName() == null || user.getEmail() == null) {
            return ResponseEntity.badRequest().build(); // Returns 400 Bad Request if data is invalid
        }
        return ResponseEntity.status(201).body(userService.saveUser(user)); // Returns 201 Created
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.status(302).body("User deleted successfully!"); // Returns 302 Found
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Returns 404 if user not found
        }
    }
}

