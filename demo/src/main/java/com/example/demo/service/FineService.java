package com.example.demo.service;

import com.example.demo.dto.request.PayFineRequest;
import com.example.demo.dto.response.FineResponse;
import com.example.demo.model.Fine;
import com.example.demo.model.Loan;
import com.example.demo.repository.FineRepository;
import com.example.demo.repository.LoanRepository;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FineService {
    FineRepository fineRepository;
    LoanRepository loanRepository;

    @Transactional
    public FineResponse generateFine(String loanId, BigDecimal dailyRate) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        if (loan.getReturnedAt() == null) {
            throw new AppException(ErrorCode.LOAN_NOT_RETURNED);
        }

        LocalDateTime dueAt = loan.getDueAt();
        LocalDateTime returnedAt = loan.getReturnedAt();

        if (returnedAt.isBefore(dueAt.toLocalDate().atStartOfDay())) {
            return null;
        }

        long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(dueAt, returnedAt);
        BigDecimal amount = dailyRate.multiply(BigDecimal.valueOf(Math.max(0, daysOverdue)));

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return null;
        }

        Fine fine = Fine.builder()
                .user(loan.getUser())
                .loan(loan)
                .amount(amount)
                .paid(false)
                .createdAt(LocalDate.now())
                .build();

        return toResponse(fineRepository.save(fine));
    }

    @Transactional
    public FineResponse payFine(PayFineRequest request, NotificationService notificationService) {
        Fine fine = fineRepository.findById(request.getFineId())
                .orElseThrow(() -> new AppException(ErrorCode.FINE_NOT_FOUND));

        if (fine.isPaid()) {
            throw new AppException(ErrorCode.FINE_ALREADY_PAID);
        }

        if (request.getAmount().compareTo(fine.getAmount()) < 0) {
            throw new AppException(ErrorCode.FINE_INSUFFICIENT_AMOUNT);
        }

        fine.setPaid(true);
        fine.setPaidAt(LocalDate.now());

        notificationService.createNotification(
                fine.getUser().getId(),
                "Tiền phạt đã thanh toán",
                "Thanh toán tiền phạt " + fine.getAmount() + " đã hoàn tất."
        );

        return toResponse(fineRepository.save(fine));
    }

    public Page<FineResponse> getUserFines(String userId, Pageable pageable) {
        Specification<Fine> spec = (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);

        return fineRepository.findAll(spec, pageable)
                .map(this::toResponse);
    }

    private FineResponse toResponse(Fine fine) {
        return FineResponse.builder()
                .fineId(fine.getId())
                .userId(fine.getUser().getId())
                .loanId(fine.getLoan().getId())
                .amount(fine.getAmount())
                .paid(fine.isPaid())
                .createdAt(fine.getCreatedAt())
                .paidAt(fine.getPaidAt())
                .build();
    }
}
