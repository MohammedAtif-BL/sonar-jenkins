package com.example.spring_redis.service;

import com.example.spring_redis.entity.User;
import com.example.spring_redis.exception.UserNotFoundException;
import com.example.spring_redis.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testGetAllUsers_Success() {
        List<User> users = Arrays.asList(
                new User(1L, "John Doe", "john@example.com"),
                new User(2L, "Jane Doe", "jane@example.com")
        );
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
    }

    @Test
    void testGetUserById_UserExists() {
        Long userId = 1L;
        User user = new User(userId, "John Doe", "john@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(userId);

        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
    }

    @Test
    void testGetUserById_UserNotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<User> result = userService.getUserById(userId);

        assertFalse(result.isPresent());
    }

    @Test
    void testSaveUser_Success() {
        User user = new User(1L, "John Doe", "john@example.com");
        when(userRepository.save(any(User.class))).thenReturn(user);

        User result = userService.saveUser(user);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    @Test
    void testDeleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
    }
}

