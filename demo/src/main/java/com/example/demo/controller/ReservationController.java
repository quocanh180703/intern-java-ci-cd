package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.request.ReserveItemRequest;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.service.ReservationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationController {
    ReservationService reservationService;

    @PostMapping("/reserve")
    ApiResponse<ReservationResponse> reserveItem(@RequestBody @Valid ReserveItemRequest request) {
        return ApiResponse.<ReservationResponse>builder()
                .result(reservationService.reserveItem(request))
                .build();
    }
    
    @PostMapping("/{reservationId}/fulfill")
    ApiResponse<String> fulfillReservation(@PathVariable String reservationId) {
        reservationService.fulfillReservation(reservationId);
        return ApiResponse.<String>builder()
                .result("Reservation fulfilled successfully")
                .build();
    }

    @DeleteMapping("/{reservationId}/cancel")
    ApiResponse<String> cancelReservation(@PathVariable String reservationId) {
        reservationService.cancelReservation(reservationId);
        return ApiResponse.<String>builder()
                .result("Reservation cancelled")
                .build();
    }

    @GetMapping
    ApiResponse<Page<ReservationResponse>> getReservations(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) ReservationStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "priority") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return ApiResponse.<Page<ReservationResponse>>builder()
                .result(reservationService.getReservations(userId, status, pageable))
                .build();
    }
}
