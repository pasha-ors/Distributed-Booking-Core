package com.booking.order.dto;

public record PaymentResponse(
        Long id,
        Long orderId,
        String status
) {}
