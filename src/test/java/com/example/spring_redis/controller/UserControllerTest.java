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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.*;

@ExtendWith(MockitoExtension.class)  // Use Mockito extension
class UserControllerTest {

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
        when(userService.saveUser(Mockito.any(User.class)))
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
        when(userService.getUserById(1L))
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
        when(userService.getUserById(1L))
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

    @Test
    void testGetAllUsers() throws Exception {
        // Mock Data
        List<User> mockUsers = Arrays.asList(
                new User(1L, "John Doe", "john@example.com"),
                new User(2L, "Jane Doe", "jane@example.com")
        );

        // Mock Service Call
        when(userService.getAllUsers()).thenReturn(mockUsers);

        // Perform GET Request
        mockMvc.perform(MockMvcRequestBuilders.get("/users")) // Adjust if your endpoint is different
                .andExpect(MockMvcResultMatchers.status().isOk()) // Check HTTP 200
                .andExpect(jsonPath("$.size()", is(2))) // Check list size
                .andExpect(jsonPath("$[0].id", is(1))) // Validate first user ID
                .andExpect(jsonPath("$[0].name", is("John Doe"))) // Validate first user name
                .andExpect(jsonPath("$[1].id", is(2))) // Validate second user ID
                .andExpect(jsonPath("$[1].name", is("Jane Doe"))); // Validate second user name
    }

    @Test
    void testSettersAndGetters() {
        user = new User();

        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");

        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
    }

}
