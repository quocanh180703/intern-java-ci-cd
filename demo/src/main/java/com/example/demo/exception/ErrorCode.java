package com.example.demo.exception;

public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999,"Uncategorized error"),
    USER_EXITED(500, "User exited"),
    USERNAME_INVALID(1003, "Username must be at last 3 character"),
    INVALID_PASSWORD(1004, "Password must be at least 8 character"),
    USER_NOT_EXITED(501, "User not exited"),
    UNAUTHENTICATED(502, "Unauthenticated"),
    ;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
