package com.booking.payment.entity;

public enum PaymentStatus {
    INIT,
    PROCESSING,
    SUCCESS,
    FAILED,
    FAILED_FINAL,
    CANCELLED
}
