package com.example.demo.service;

import com.example.demo.dto.request.BorrowItemRequest;
import com.example.demo.dto.response.LoanResponse;
import com.example.demo.enums.LoanStatus;
import com.example.demo.enums.ReservationStatus;
import com.example.demo.exception.AppException;
import com.example.demo.exception.ErrorCode;
import com.example.demo.model.Item;
import com.example.demo.model.Loan;
import com.example.demo.model.Reservation;
import com.example.demo.model.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.LoanRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
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
public class LoanService {
    LoanRepository loanRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;
    ReservationRepository reservationRepository;
    ReservationService reservationService;

    @Transactional
    public LoanResponse borrowItem(BorrowItemRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(ErrorCode.ITEM_NOT_FOUND));

        if (item.getAvailableCopies() <= 0) {
            throw new AppException(ErrorCode.ITEM_OUT_OF_STOCK);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueAt = request.getDueDate().atTime(23, 59, 59);

        if (dueAt.isBefore(now)) {
            throw new AppException(ErrorCode.INVALID_DUE_DATE);
        }

        item.setAvailableCopies(item.getAvailableCopies() - 1);
        itemRepository.save(item);

        Loan loan = Loan.builder()
                .user(user)
                .item(item)
                .status(LoanStatus.BORROWED)
                .borrowedAt(now)
                .dueAt(dueAt)
                .build();

        return toResponse(loanRepository.save(loan));
    }

    @Transactional
    public LoanResponse returnItem(String loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new AppException(ErrorCode.LOAN_NOT_FOUND));

        if (loan.getStatus() == LoanStatus.RETURNED) {
            throw new AppException(ErrorCode.LOAN_ALREADY_RETURNED);
        }

        loan.setStatus(LoanStatus.RETURNED);
        loan.setReturnedAt(LocalDateTime.now());

        Item item = loan.getItem();
        int nextAvailable = Math.min(item.getAvailableCopies() + 1, item.getTotalCopies());
        item.setAvailableCopies(nextAvailable);

        itemRepository.save(item);
        loanRepository.save(loan);

        reservationService.fulfillNextReservation(item.getId());

        return toResponse(loan);
    }

    public Page<LoanResponse> getLoans(String userId, LoanStatus status, Pageable pageable) {
        Specification<Loan> specification = Specification.where(null);

        if (userId != null && !userId.isBlank()) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("user").get("id"), userId));
        }

        if (status != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        return loanRepository.findAll(specification, pageable)
                .map(this::toResponse);
    }

    private LoanResponse toResponse(Loan loan) {
        return LoanResponse.builder()
                .loanId(loan.getId())
                .userId(loan.getUser().getId())
                .itemId(loan.getItem().getId())
                .status(loan.getStatus())
                .borrowedAt(loan.getBorrowedAt())
                .dueAt(loan.getDueAt())
                .returnedAt(loan.getReturnedAt())
                .build();
    }

}
