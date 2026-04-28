package com.example.demo.controller;

import com.example.demo.dto.request.BorrowItemRequest;
import com.example.demo.dto.response.LoanResponse;
import com.example.demo.enums.LoanStatus;
import com.example.demo.service.LoanService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoanController.class)
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void borrowItem_success() throws Exception {
        BorrowItemRequest request = BorrowItemRequest.builder()
                .userId("u1")
                .itemId(1)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        LoanResponse response = LoanResponse.builder()
                .loanId("l1")
                .userId("u1")
                .itemId(1)
                .status(LoanStatus.BORROWED)
                .borrowedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusDays(7))
                .build();

        Mockito.when(loanService.borrowItem(any())).thenReturn(response);

        mockMvc.perform(post("/loans/borrow")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.loanId").value("l1"))
                .andExpect(jsonPath("$.result.status").value("BORROWED"));
    }

    @Test
    @WithMockUser
    void returnItem_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .loanId("l1")
                .userId("u1")
                .itemId(1)
                .status(LoanStatus.RETURNED)
                .borrowedAt(LocalDateTime.now().minusDays(1))
                .dueAt(LocalDateTime.now().plusDays(6))
                .returnedAt(LocalDateTime.now())
                .build();

        Mockito.when(loanService.returnItem("l1")).thenReturn(response);

        mockMvc.perform(post("/loans/l1/return").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.status").value("RETURNED"));
    }

    @Test
    @WithMockUser
    void getLoans_success() throws Exception {
        LoanResponse response = LoanResponse.builder()
                .loanId("l1")
                .userId("u1")
                .itemId(1)
                .status(LoanStatus.BORROWED)
                .borrowedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusDays(5))
                .build();

        Mockito.when(loanService.getLoans(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/loans"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].loanId").value("l1"));
    }
}
