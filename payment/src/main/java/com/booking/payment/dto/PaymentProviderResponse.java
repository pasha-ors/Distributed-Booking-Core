package com.booking.payment.dto;

public record PaymentProviderResponse(
        boolean success,
        String externalId,
        String errorMessage
) {}
