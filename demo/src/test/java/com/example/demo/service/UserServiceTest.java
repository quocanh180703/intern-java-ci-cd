package com.example.demo.service;

import com.example.demo.dto.request.UserCreationRequest;
import com.example.demo.dto.request.UserUpdateRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.exception.AppException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.View;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private View error;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId("1");
        user.setUsername("test");

        userResponse = new UserResponse();
        userResponse.setUsername("test");
    }

    @Test
    void testCreateUser_success() {
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("test");
        request.setPassword("123");

        when(userRepository.existsByUsername("test")).thenReturn(false);
        when(userMapper.toUser(request)).thenReturn(user);
        when(passwordEncoder.encode("123")).thenReturn("encoded");
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(request);

        assertNotNull(result);
        assertEquals("test", result.getUsername());

        verify(userRepository).save(user);
        verify(passwordEncoder).encode("123");
    }

    @Test
    void testCreateUser_userExists() {
        UserCreationRequest request = new UserCreationRequest();
        request.setUsername("test");

        when(userRepository.existsByUsername("test")).thenReturn(true);

        assertThrows(AppException.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    void testGetMyInfo_success() {
        // mock SecurityContext
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("test")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getMyInfo();

        assertEquals("test", result.getUsername());
    }

    @Test
    void testGetMyInfo_notFound() {
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("test");

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername("test")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> {
            userService.getMyInfo();
        });
    }

    @Test
    void testUpdateUser_success() {
        UserUpdateRequest request = new UserUpdateRequest();

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        doNothing().when(userMapper).updateUser(user, request);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.updateUser("1", request);

        assertNotNull(result);
        verify(userMapper).updateUser(user, request);
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUser_notFound() {
        UserUpdateRequest request = new UserUpdateRequest();

        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            userService.updateUser("1", request);
        });
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById("1");

        userService.deleteUser("1");

        verify(userRepository).deleteById("1");
    }

    @Test
    void testGetUsers() {
        List<User> users = List.of(user);

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        List<UserResponse> result = userService.getUsers();

        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void testGetUser_success() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUser("1");

        assertEquals("test", result.getUsername());
    }

    @Test
    void testGetUser_notFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> {
            userService.getUser("1");
        });
    }
}