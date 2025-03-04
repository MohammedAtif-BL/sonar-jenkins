package com.example.spring_redis.service;

import org.springframework.stereotype.Service;
import com.example.spring_redis.entity.User;
import com.example.spring_redis.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Fetch all users (No Caching)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID with Caching
    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Save new user and update cache
    @CachePut(value = "users", key = "#user.id")
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Delete user and remove from cache
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found"); // Ensures exception handling in controller
        }
        userRepository.deleteById(id);
    }
}

