package com.example.demo.service;

import com.example.demo.dto.request.ReserveItemRequest;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.exception.AppException;
import com.example.demo.model.Item;
import com.example.demo.model.Reservation;
import com.example.demo.model.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private ReservationService reservationService;

    private User user;
    private Item item;

    @BeforeEach
    void setUp() {
        user = User.builder().id("u1").username("test").build();
        item = new Item("Java Core", "Book", "Author A");
        item.setId(1);
    }

    @Test
    void reserveItem_success() {
        ReserveItemRequest request = ReserveItemRequest.builder()
                .userId("u1")
                .itemId(1)
                .build();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(itemRepository.findById(1)).thenReturn(Optional.of(item));
        when(reservationRepository.countHigherPriority(1, Integer.MAX_VALUE)).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setId("r1");
            return r;
        });

        var response = reservationService.reserveItem(request);

        assertEquals("r1", response.getReservationId());
        assertEquals(ReservationStatus.PENDING, response.getStatus());
        assertEquals(1, response.getPriority());
    }

    @Test
    void cancelReservation_success() {
        Reservation reservation = Reservation.builder()
                .id("r1")
                .user(user)
                .item(item)
                .status(ReservationStatus.PENDING)
                .priority(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(reservationRepository.findById("r1")).thenReturn(Optional.of(reservation));

        reservationService.cancelReservation("r1");

        assertEquals(ReservationStatus.CANCELLED, reservation.getStatus());
    }

    @Test
    void fulfillNextReservation_success() {
        Reservation reservation = Reservation.builder()
                .id("r1")
                .user(user)
                .item(item)
                .status(ReservationStatus.PENDING)
                .priority(1)
                .createdAt(LocalDateTime.now())
                .build();

        when(reservationRepository.findByItemIdAndStatusOrderByPriorityAsc(1, ReservationStatus.PENDING))
                .thenReturn(java.util.List.of(reservation));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        reservationService.fulfillNextReservation(1);

        assertEquals(ReservationStatus.FULFILLED, reservation.getStatus());
        assertNotNull(reservation.getFulfilledAt());
    }
}
