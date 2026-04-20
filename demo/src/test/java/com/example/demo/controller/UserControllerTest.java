package com.example.demo.controller;

import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void testCreateUser() throws Exception {
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("testuser");
        request.setPassword("12345678");
        request.setFirstname("A");
        request.setLastname("B");

        UserResponse response = new UserResponse();
        response.setUsername("testuser");

        when(userService.createUser(Mockito.any())).thenReturn(response);

        mockMvc.perform(post("/users")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("testuser"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUsers() throws Exception {
        UserResponse user = new UserResponse();
        user.setUsername("admin");

        when(userService.getUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].username").value("admin"));
    }

    @Test
    @WithMockUser(username = "test")
    void testGetUser() throws Exception {
        UserResponse response = new UserResponse();
        response.setUsername("test");

        when(userService.getUser("1")).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("test"));
    }

    @Test
    @WithMockUser(username = "test")
    void testGetMyInfo() throws Exception {
        UserResponse response = new UserResponse();
        response.setUsername("test");

        when(userService.getMyInfo()).thenReturn(response);

        mockMvc.perform(get("/users/myInfo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("test"));
    }

    @Test
    @WithMockUser(username = "test")
    void testUpdateUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest();

        UserResponse response = new UserResponse();
        response.setUsername("updated");

        when(userService.updateUser(Mockito.eq("1"), Mockito.any()))
                .thenReturn(response);

        mockMvc.perform(put("/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.username").value("updated"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("User has been deleted"));
    }
}