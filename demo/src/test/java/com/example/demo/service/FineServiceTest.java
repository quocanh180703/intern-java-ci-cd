package com.example.demo.service;

import com.example.demo.dto.request.PayFineRequest;
import com.example.demo.model.Fine;
import com.example.demo.model.Loan;
import com.example.demo.model.User;
import com.example.demo.repository.FineRepository;
import com.example.demo.repository.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FineServiceTest {

    @Mock
    private FineRepository fineRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private FineService fineService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder().id("u1").username("test").build();
    }

    @Test
    void payFine_success() {
        Fine fine = Fine.builder()
                .id("f1")
                .user(user)
            .loan(org.mockito.Mockito.mock(com.example.demo.model.Loan.class))
                .amount(BigDecimal.valueOf(10))
                .paid(false)
                .createdAt(LocalDate.now())
                .build();

        org.mockito.Mockito.when(fine.getLoan().getId()).thenReturn("l1");

        PayFineRequest request = PayFineRequest.builder()
                .fineId("f1")
                .amount(BigDecimal.valueOf(10))
                .build();

        when(fineRepository.findById("f1")).thenReturn(Optional.of(fine));
        when(fineRepository.save(any(Fine.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = fineService.payFine(request, notificationService);

        assertTrue(response.isPaid());
    }
}
