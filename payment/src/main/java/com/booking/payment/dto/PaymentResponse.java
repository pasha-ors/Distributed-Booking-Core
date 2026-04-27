package com.booking.payment.dto;

public record PaymentResponse(
    Long id,
    String status,
    String externalId
) {}
