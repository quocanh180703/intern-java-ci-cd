package com.example.demo.model;

import com.example.demo.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_user_status", columnList = "user_id,status"),
    @Index(name = "idx_item_status", columnList = "item_id,status")
})
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    Item item;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    ReservationStatus status;

    @Column(nullable = false)
    LocalDateTime createdAt;

    LocalDateTime fulfilledAt;

    @Column(nullable = false)
    int priority;
}
