package com.example.demo.service;

import com.example.demo.dto.request.BorrowItemRequest;
import com.example.demo.enums.LoanStatus;
import com.example.demo.exception.AppException;
import com.example.demo.model.Item;
import com.example.demo.model.Loan;
import com.example.demo.model.User;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private LoanService loanService;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder().id("u1").username("test").build();

        item = new Item("Java Core", "Book", "Author A");
        item.setId(1);
        item.setTotalCopies(3);
        item.setAvailableCopies(2);
    }

    @Test
    void borrowItem_success() {
        BorrowItemRequest request = BorrowItemRequest.builder()
                .userId("u1")
                .itemId(1)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> {
            Loan l = invocation.getArgument(0);
            l.setId("l1");
            return l;
        });

        var response = loanService.borrowItem(request);

        assertEquals("l1", response.getLoanId());
        assertEquals(LoanStatus.BORROWED, response.getStatus());
        assertEquals(1, item.getAvailableCopies());
    }

    @Test
    void borrowItem_outOfStock() {
        item.setAvailableCopies(0);
        BorrowItemRequest request = BorrowItemRequest.builder()
                .userId("u1")
                .itemId(1)
                .dueDate(LocalDate.now().plusDays(7))
                .build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));

        assertThrows(AppException.class, () -> loanService.borrowItem(request));
    }

    @Test
    void returnItem_success() {
        Loan loan = Loan.builder()
                .id("l1")
                .user(user)
                .item(item)
                .status(LoanStatus.BORROWED)
                .borrowedAt(LocalDateTime.now().minusDays(1))
                .dueAt(LocalDateTime.now().plusDays(6))
                .build();

        when(loanRepository.findById("l1")).thenReturn(Optional.of(loan));
        when(itemRepository.save(any(Item.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(loanRepository.save(any(Loan.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = loanService.returnItem("l1");

        assertEquals(LoanStatus.RETURNED, response.getStatus());
        assertNotNull(response.getReturnedAt());
        verify(reservationService).fulfillNextReservation(1);
    }

    @Test
    void getLoans_filterAndPage_success() {
        Loan loan = Loan.builder()
                .id("l1")
                .user(user)
                .item(item)
                .status(LoanStatus.BORROWED)
                .borrowedAt(LocalDateTime.now())
                .dueAt(LocalDateTime.now().plusDays(5))
                .build();

        when(loanRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(loan)));

        Page<?> page = loanService.getLoans("u1", LoanStatus.BORROWED, PageRequest.of(0, 10));

        assertEquals(1, page.getTotalElements());
    }
}
