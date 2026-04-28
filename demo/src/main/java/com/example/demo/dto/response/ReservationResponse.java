package com.example.demo.dto.response;

import com.example.demo.enums.ReservationStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReservationResponse {
    String reservationId;
    String userId;
    int itemId;
    ReservationStatus status;
    int priority;
    LocalDateTime createdAt;
    LocalDateTime fulfilledAt;
}
