package com.booking.payment.config;

import com.booking.payment.entity.Payment;
import com.booking.payment.entity.PaymentStatus;
import com.booking.payment.repository.PaymentRepository;
import com.booking.payment.service.PaymentAsyncService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRetryScheduler {

    private final PaymentRepository paymentRepository;
    private final PaymentAsyncService paymentAsyncService;

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void retryPayment() {

        var payments = paymentRepository.findPaymentsForRetry();

        log.info("Retry batch size: {}", payments.size());

        payments.forEach(p ->
                paymentAsyncService.processPaymentAsync(p.getId())
        );
    }
}
