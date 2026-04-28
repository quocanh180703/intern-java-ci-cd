package com.example.demo.controller;

import com.example.demo.dto.response.NotificationResponse;
import com.example.demo.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @Test
    @WithMockUser
    void markAsRead_success() throws Exception {
        NotificationResponse response = NotificationResponse.builder()
                .notificationId("n1")
                .userId("u1")
                .title("Test")
                .message("Message")
                .read(true)
                .createdAt(LocalDateTime.now())
                .readAt(LocalDateTime.now())
                .build();

        Mockito.when(notificationService.markAsRead("n1")).thenReturn(response);

        mockMvc.perform(post("/notifications/n1/mark-read").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.notificationId").value("n1"));
    }

    @Test
    @WithMockUser
    void getUserNotifications_success() throws Exception {
        NotificationResponse response = NotificationResponse.builder()
                .notificationId("n1")
                .userId("u1")
                .title("Test")
                .message("Message")
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(notificationService.getUserNotifications(any(), any()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/notifications").param("userId", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].notificationId").value("n1"));
    }
}
