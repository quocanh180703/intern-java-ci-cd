package com.example.demo.controller;

import com.example.demo.dto.request.ReserveItemRequest;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.service.ReservationService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser
    void reserveItem_success() throws Exception {
        ReserveItemRequest request = ReserveItemRequest.builder()
                .userId("u1")
                .itemId(1)
                .build();

        ReservationResponse response = ReservationResponse.builder()
                .reservationId("r1")
                .userId("u1")
                .itemId(1)
                .status(ReservationStatus.PENDING)
                .priority(1)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(reservationService.reserveItem(any())).thenReturn(response);

        mockMvc.perform(post("/reservations/reserve")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.reservationId").value("r1"));
    }

    @Test
    @WithMockUser
    void cancelReservation_success() throws Exception {
        mockMvc.perform(delete("/reservations/r1/cancel").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getReservations_success() throws Exception {
        ReservationResponse response = ReservationResponse.builder()
                .reservationId("r1")
                .userId("u1")
                .itemId(1)
                .status(ReservationStatus.PENDING)
                .priority(1)
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(reservationService.getReservations(any(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(response)));

        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].reservationId").value("r1"));
    }
}
