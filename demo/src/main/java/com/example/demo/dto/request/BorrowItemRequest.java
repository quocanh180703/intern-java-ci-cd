package com.example.demo.dto.request;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BorrowItemRequest {
    @NotBlank
    String userId;

    @NotNull
    Integer itemId;

    @NotNull
    @FutureOrPresent
    LocalDate dueDate;
}
