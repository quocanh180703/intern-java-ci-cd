package com.example.demo.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FineResponse {
    String fineId;
    String userId;
    String loanId;
    BigDecimal amount;
    boolean paid;
    LocalDate createdAt;
    LocalDate paidAt;
}
