package com.example.demo.model;

import com.example.demo.enums.LoanStatus;
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
@Table(name = "loans")
public class Loan {
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
    LoanStatus status;

    @Column(nullable = false)
    LocalDateTime borrowedAt;

    @Column(nullable = false)
    LocalDateTime dueAt;

    LocalDateTime returnedAt;
}
