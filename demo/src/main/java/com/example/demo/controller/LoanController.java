package com.example.demo.controller;

import com.example.demo.dto.request.ApiResponse;
import com.example.demo.dto.request.BorrowItemRequest;
import com.example.demo.dto.response.LoanResponse;
import com.example.demo.enums.LoanStatus;
import com.example.demo.service.LoanService;
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
@RequestMapping("/loans")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoanController {
    LoanService loanService;

    @PostMapping("/borrow")
    ApiResponse<LoanResponse> borrowItem(@RequestBody @Valid BorrowItemRequest request) {
        return ApiResponse.<LoanResponse>builder()
                .result(loanService.borrowItem(request))
                .build();
    }

    @PostMapping("/{loanId}/return")
    ApiResponse<LoanResponse> returnItem(@PathVariable String loanId) {
        return ApiResponse.<LoanResponse>builder()
                .result(loanService.returnItem(loanId))
                .build();
    }

    @GetMapping
    ApiResponse<Page<LoanResponse>> getLoans(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) LoanStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "borrowedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort.Direction sortDirection = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        return ApiResponse.<Page<LoanResponse>>builder()
                .result(loanService.getLoans(userId, status, pageable))
                .build();
    }
}
