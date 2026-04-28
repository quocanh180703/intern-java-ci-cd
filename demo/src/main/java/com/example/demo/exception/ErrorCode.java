package com.example.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1006, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1007, "You do not have permission", HttpStatus.FORBIDDEN),
    INVALID_DOB(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),

    // Item & Loan
    ITEM_NOT_FOUND(503, "Item not found", HttpStatus.NOT_FOUND),
    ITEM_OUT_OF_STOCK(504, "Item is out of stock", HttpStatus.BAD_REQUEST),
    INVALID_DUE_DATE(505, "Due date must be today or in the future", HttpStatus.BAD_REQUEST),
    LOAN_NOT_FOUND(506, "Loan not found", HttpStatus.NOT_FOUND),
    LOAN_ALREADY_RETURNED(507, "Loan already returned", HttpStatus.BAD_REQUEST),
    LOAN_NOT_RETURNED(508, "Loan not returned", HttpStatus.BAD_REQUEST),

    // Reservation
    RESERVATION_NOT_FOUND(509, "Reservation not found", HttpStatus.NOT_FOUND),
    RESERVATION_ALREADY_FULFILLED(510, "Reservation already fulfilled", HttpStatus.BAD_REQUEST),

    // Fine
    FINE_NOT_FOUND(511, "Fine not found", HttpStatus.NOT_FOUND),
    FINE_ALREADY_PAID(512, "Fine already paid", HttpStatus.BAD_REQUEST),
    FINE_INSUFFICIENT_AMOUNT(513, "Payment amount is insufficient", HttpStatus.BAD_REQUEST),

    // Notification
    NOTIFICATION_NOT_FOUND(514, "Notification not found", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;
}