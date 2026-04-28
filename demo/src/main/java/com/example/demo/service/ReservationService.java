package com.example.demo.service;

import com.example.demo.dto.request.ReserveItemRequest;
import com.example.demo.dto.response.ReservationResponse;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.model.Item;
import com.example.demo.model.Reservation;
import com.example.demo.model.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReservationService {
    ReservationRepository reservationRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    NotificationService notificationService;

    @Transactional
    public ReservationResponse reserveItem(ReserveItemRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        int priority = reservationRepository.countHigherPriority(item.getId(), Integer.MAX_VALUE) + 1;

        Reservation reservation = Reservation.builder()
                .user(user)
                .item(item)
                .status(ReservationStatus.PENDING)
                .priority(priority)
                .createdAt(LocalDateTime.now())
                .build();

        notificationService.createNotification(
                user.getId(),
                "Đặt chỗ thành công",
                "Bạn đã đặt chỗ '" + item.getTitle() + "' với độ ưu tiên " + priority
        );

        return toResponse(reservationRepository.save(reservation));
    }

    @Transactional
    public void fulfillReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

                fulfillReservation(reservation);
        }

        @Transactional
        public void fulfillNextReservation(int itemId) {
                java.util.List<Reservation> reservations = reservationRepository.findByItemIdAndStatusOrderByPriorityAsc(
                                itemId,
                                ReservationStatus.PENDING
                );

                if (!reservations.isEmpty()) {
                        fulfillReservation(reservations.get(0));
                }
    }

    @Transactional
    public void cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() == ReservationStatus.FULFILLED) {
            throw new AppException(ErrorCode.RESERVATION_ALREADY_FULFILLED);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        notificationService.createNotification(
                reservation.getUser().getId(),
                "Đặt chỗ bị hủy",
                "Đặt chỗ '" + reservation.getItem().getTitle() + "' đã bị hủy"
        );

        reservationRepository.save(reservation);
    }

    public Page<ReservationResponse> getReservations(String userId, ReservationStatus status, Pageable pageable) {
        Specification<Reservation> spec = Specification.where(null);

        if (userId != null && !userId.isBlank()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return reservationRepository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .reservationId(reservation.getId())
                .userId(reservation.getUser().getId())
                .itemId(reservation.getItem().getId())
                .status(reservation.getStatus())
                .priority(reservation.getPriority())
                .createdAt(reservation.getCreatedAt())
                .fulfilledAt(reservation.getFulfilledAt())
                .build();
    }

        private void fulfillReservation(Reservation reservation) {
                reservation.setStatus(ReservationStatus.FULFILLED);
                reservation.setFulfilledAt(LocalDateTime.now());

                notificationService.createNotification(
                                reservation.getUser().getId(),
                                "Đặt chỗ đã được thực hiện",
                                "Bạn có thể mượn '" + reservation.getItem().getTitle() + "' bây giờ"
                );

                reservationRepository.save(reservation);
        }
}
