package com.booking.payment.service;

import com.booking.payment.entity.Payment;
import com.booking.payment.entity.PaymentStatus;
import com.booking.payment.provider.PaymentProviderClient;
import com.booking.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;


@Service
@RequiredArgsConstructor
public class PaymentAsyncService {

    private final PaymentRepository paymentRepository;
    private final PaymentProviderClient paymentProviderClient;

    private static final int MAX_RETRIES = 3;
    private static final Duration MAX_DURATION = Duration.ofMinutes(10);

    @Async
    @Transactional
    public void processPaymentAsync(Long paymentId){

        Payment payment = paymentRepository
                .findByIdForUpdate(paymentId)
                .orElseThrow();

        if (payment.getStatus() != PaymentStatus.PROCESSING) {
            return;
        }

        try {
            var response = paymentProviderClient.charge(payment);

            if (response.success()) {
                payment.setStatus(PaymentStatus.SUCCESS); // UPDATE SUCCESS
                payment.setExternalId(response.externalId());
            } else {
                handleFailure(payment, response.errorMessage());
            }

        }catch (Exception ex){
            handleFailure(payment, ex.getMessage());
        }

        paymentRepository.save(payment);
    }

    private void handleFailure(Payment payment, String error){

        payment.setFailureReason(error);

        if (payment.getRetryCount() >= MAX_RETRIES) {
            payment.setStatus(PaymentStatus.FAILED);
            return;
        }

        if (payment.getCreatedAt()
                .plus(MAX_DURATION)
                .isBefore(LocalDateTime.now())) {

            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason("Timeout exceeded");
            return;
        }

        int retries = payment.getRetryCount();
        payment.setRetryCount(retries + 1);

        long base = (long) Math.pow(2, retries);
        long jitter = ThreadLocalRandom.current().nextLong(0, 3);

        payment.setNextRetryAt(
                LocalDateTime.now().plusSeconds(base + jitter)
        );
    }
}
