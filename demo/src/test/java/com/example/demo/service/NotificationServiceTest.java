package com.example.demo.service;

import com.example.demo.model.Notification;
import com.example.demo.model.User;
import com.example.demo.repository.NotificationRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id("u1").username("test").build();
    }

    @Test
    void createNotification_success() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId("n1");
            return n;
        });

        var response = notificationService.createNotification("u1", "Test", "Message");

        assertEquals("n1", response.getNotificationId());
        assertFalse(response.isRead());
    }

    @Test
    void markAsRead_success() {
        Notification notification = Notification.builder()
                .id("n1")
                .user(user)
                .title("Test")
                .message("Message")
                .read(false)
                .build();

        when(notificationRepository.findById("n1")).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = notificationService.markAsRead("n1");

        assertTrue(response.isRead());
    }
}
