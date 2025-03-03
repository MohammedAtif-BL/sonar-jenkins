package com.example.spring_redis.controller;

import com.example.spring_redis.entity.User;
import com.example.spring_redis.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)  // Use Mockito extension
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks  // Injects the mocked service into controller
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();  // Setup MockMvc manually
        user = new User(1L, "John Doe", "john.doe@example.com");
    }

    @Test
    void testCreateUser() throws Exception {
        Mockito.when(userService.saveUser(Mockito.any(User.class)))
                .thenReturn(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(user)))
                .andDo(MockMvcResultHandlers.print())  // Debugging
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void testGetUserById() throws Exception {
        Mockito.when(userService.getUserById(1L))
                .thenReturn(Optional.of(user));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andDo(MockMvcResultHandlers.print())  // Debugging
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(user.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(user.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void testDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andDo(MockMvcResultHandlers.print())  // Debugging
                .andExpect(MockMvcResultMatchers.status().isFound());
    }

    @Test
    void testGetUserFromCache() throws Exception {
        // Use `thenAnswer` to simulate caching (return the same response without re-invoking the method)
        Mockito.when(userService.getUserById(1L))
                .thenAnswer(invocation -> Optional.of(user));

        // First call - should hit the database
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Reset interactions to ensure we measure only the second request
        Mockito.reset(userService);

        // Second call - should be served from cache (no new interaction with service)
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Now verify it was only invoked ONCE (before the reset)
        Mockito.verify(userService, Mockito.times(1)).getUserById(1L);
    }

}
