package com.booking.payment.provider;

import com.booking.payment.dto.PaymentProviderResponse;
import com.booking.payment.entity.Payment;

public interface PaymentProviderClient {
    PaymentProviderResponse charge(Payment payment);
}
