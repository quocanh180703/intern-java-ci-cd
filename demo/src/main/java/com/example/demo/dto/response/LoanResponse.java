package com.example.demo.dto.response;

import com.example.demo.enums.LoanStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoanResponse {
    String loanId;
    String userId;
    int itemId;
    LoanStatus status;
    LocalDateTime borrowedAt;
    LocalDateTime dueAt;
    LocalDateTime returnedAt;
}
