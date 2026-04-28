package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.request.PayFineRequest;
import com.example.demo.dto.response.FineResponse;
import com.example.demo.service.FineService;
import com.example.demo.service.NotificationService;
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
@RequestMapping("/fines")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineController {
    FineService fineService;
    NotificationService notificationService;

    @PostMapping("/{fineId}/pay")
    ApiResponse<FineResponse> payFine(@PathVariable String fineId, @RequestBody @Valid PayFineRequest request) {
        request.setFineId(fineId);
        return ApiResponse.<FineResponse>builder()
                .result(fineService.payFine(request, notificationService))
                .build();
    }

    @GetMapping
    ApiResponse<Page<FineResponse>> getUserFines(
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return ApiResponse.<Page<FineResponse>>builder()
                .result(fineService.getUserFines(userId, pageable))
                .build();
    }
}
