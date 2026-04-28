package com.example.demo.controller;

import com.example.demo.dto.request.PayFineRequest;
import com.example.demo.dto.response.FineResponse;
import com.example.demo.service.FineService;
import com.example.demo.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FineController.class)
class FineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FineService fineService;

    @MockBean
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void payFine_success() throws Exception {
        PayFineRequest request = PayFineRequest.builder()
                .fineId("f1")
                .amount(BigDecimal.valueOf(10))
                .build();

        FineResponse response = FineResponse.builder()
                .fineId("f1")
                .userId("u1")
                .loanId("l1")
                .amount(BigDecimal.valueOf(10))
                .paid(true)
                .createdAt(LocalDate.now())
                .paidAt(LocalDate.now())
                .build();

        Mockito.when(fineService.payFine(any(PayFineRequest.class), any())).thenReturn(response);

        mockMvc.perform(post("/fines/f1/pay")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.fineId").value("f1"));
    }

    @Test
    @WithMockUser
    void getUserFines_success() throws Exception {
        FineResponse response = FineResponse.builder()
                .fineId("f1")
                .userId("u1")
                .loanId("l1")
                .amount(BigDecimal.valueOf(10))
                .paid(false)
                .createdAt(LocalDate.now())
                .build();

        Mockito.when(fineService.getUserFines(any(), any()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/fines").param("userId", "u1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].fineId").value("f1"));
    }
}
