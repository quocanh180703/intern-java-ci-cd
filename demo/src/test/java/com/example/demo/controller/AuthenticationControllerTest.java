package com.example.demo.controller;

import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.IntrospectResponse;
import com.example.demo.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(AuthenticationController.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthenticationRequest authRequest;
    private AuthenticationResponse authResponse;
    private IntrospectRequest introspectRequest;
    private IntrospectResponse introspectResponse;

    @BeforeEach
    void initData() {
        authRequest = AuthenticationRequest.builder()
                .username("tung_user")
                .password("password123")
                .build();

        authResponse = AuthenticationResponse.builder()
                .token("fake-jwt-token")
                .authenticated(true)
                .build();

        introspectRequest = IntrospectRequest.builder()
                .token("fake-jwt-token")
                .build();

        introspectResponse = IntrospectResponse.builder()
                .valid(true)
                .build();
    }

    @Test
    @WithMockUser
    void authenticate_success() throws Exception {
        // Given
        String content = objectMapper.writeValueAsString(authRequest);
        Mockito.when(authenticationService.authenticate(ArgumentMatchers.any()))
                .thenReturn(authResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/token")
                        .with(csrf()) // Luôn cần nếu không disable CSRF trong SecurityConfig
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.token")
                        .value("fake-jwt-token"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.authenticated")
                        .value(true));
    }

    @Test
    @WithMockUser
    void introspect_success() throws Exception {
        // Given
        String content = objectMapper.writeValueAsString(introspectRequest);
        Mockito.when(authenticationService.introspect(ArgumentMatchers.any()))
                .thenReturn(introspectResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/introspect")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.valid")
                        .value(true));
    }
}