package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "fines", indexes = {
    @Index(name = "idx_user_paid", columnList = "user_id,is_paid"),
    @Index(name = "idx_loan", columnList = "loan_id")
})
public class Fine {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_id", nullable = false)
    Loan loan;

    @Column(nullable = false)
    BigDecimal amount;

    @Column(nullable = false, name = "is_paid")
    boolean paid = false;

    @Column(nullable = false)
    LocalDate createdAt;

    LocalDate paidAt;
}
