package com.example.demo.service;

import com.example.demo.dto.request.AuthenticationRequest;
import com.example.demo.dto.request.IntrospectRequest;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import com.nimbusds.jose.JOSEException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.ParseException;
import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private String SIGNER_KEY = "1234567890123456789012345678901234567890123456789012345678901234"; // Key tối thiểu 32 ký tự cho HS512
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private User mockUser;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(authenticationService, "SIGNER_KEY", SIGNER_KEY);

        mockUser = User.builder()
                .username("tung_user")
                .password(passwordEncoder.encode("password123")) // Mật khẩu thật là password123
                .roles(Set.of("USER"))
                .build();
    }

    @Test
    void authenticate_success() {
        AuthenticationRequest request = new AuthenticationRequest("tung_user", "password123");
        Mockito.when(userRepository.findByUsername("tung_user")).thenReturn(Optional.of(mockUser));

        var response = authenticationService.authenticate(request);

        Assertions.assertTrue(response.isAuthenticated());
        Assertions.assertNotNull(response.getToken());
    }

    @Test
    void authenticate_userNotFound() {
        AuthenticationRequest request = new AuthenticationRequest("unknown", "password");
        Mockito.when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        var exception = Assertions.assertThrows(AppException.class,
                () -> authenticationService.authenticate(request));

        Assertions.assertEquals(ErrorCode.USER_NOT_EXITED, exception.getErrorCode());
    }

    @Test
    void authenticate_wrongPassword() {
        AuthenticationRequest request = new AuthenticationRequest("tung_user", "wrong_pass");
        Mockito.when(userRepository.findByUsername("tung_user")).thenReturn(Optional.of(mockUser));

        var exception = Assertions.assertThrows(AppException.class,
                () -> authenticationService.authenticate(request));

        Assertions.assertEquals(ErrorCode.UNAUTHENTICATED, exception.getErrorCode());
    }

    @Test
    void introspect_success() throws JOSEException, ParseException {
        Mockito.when(userRepository.findByUsername("tung_user")).thenReturn(Optional.of(mockUser));

        var authResponse = authenticationService.authenticate(
                new AuthenticationRequest("tung_user", "password123")
        );

        String token = authResponse.getToken();
        IntrospectRequest introspectRequest = IntrospectRequest.builder()
                .token(token)
                .build();

        var introspectResponse = authenticationService.introspect(introspectRequest);

        Assertions.assertTrue(introspectResponse.isValid());
    }
}