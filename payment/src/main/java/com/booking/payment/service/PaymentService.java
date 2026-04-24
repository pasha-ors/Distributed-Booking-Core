package com.booking.payment.service;

import com.booking.payment.entity.Payment;
import com.booking.payment.entity.PaymentStatus;
import com.booking.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public Payment proccessPayment(Long orderId) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);

        payment.setStatus(PaymentStatus.SUCCESS);

        return paymentRepository.save(payment);
    }
}
