package com.booking.payment.provider;

import com.booking.payment.dto.PaymentProviderResponse;
import com.booking.payment.entity.Payment;

import java.util.UUID;

public class FakePaymentProviderClient implements PaymentProviderClient {

    @Override
    public PaymentProviderResponse charge(Payment payment) {
        if (payment.getAmount().doubleValue() > 1000) {
            return new PaymentProviderResponse(
                    false,
                    null,
                    "Limit exceeded"
            );
        }

        return new PaymentProviderResponse(
                true,
                "fake-" + UUID.randomUUID(),
                null
        );
    }
}
