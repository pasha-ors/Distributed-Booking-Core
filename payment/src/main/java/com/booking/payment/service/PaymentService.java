package com.booking.payment.service;

import com.booking.payment.dto.PaymentProviderResponse;
import com.booking.payment.dto.PaymentRequest;
import com.booking.payment.dto.PaymentResponse;
import com.booking.payment.entity.Payment;
import com.booking.payment.entity.PaymentStatus;
import com.booking.payment.provider.PaymentProviderClient;
import com.booking.payment.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentAsyncService paymentAsyncService;

    @Transactional
    private Payment createPayment(PaymentRequest request, String key) {

        var existing = paymentRepository.findByIdempotencyKey(key);

        if(existing.isPresent()) {
            return existing.get();
        }

        Payment payment = new Payment();
        payment.setOrderId(request.orderId());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());
        payment.setIdempotencyKey(key);

        try {
            payment = paymentRepository.save(payment); // INSERT INIT
        }catch(DataIntegrityViolationException ex) {
            return paymentRepository.findByIdempotencyKey(key)
                    .orElseThrow();
        }

        payment.setStatus(PaymentStatus.PROCESSING); // UPDATE PROCESSING
        return paymentRepository.save(payment);
    }

    public PaymentResponse processPayment(PaymentRequest request, String key) {

        Payment payment = createPayment(request, key);

        paymentAsyncService.processPaymentAsync(payment.getId());

        return mapToResponse(payment);
    }

    public PaymentResponse getPayment(Long id){
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new  PaymentResponse(
                payment.getId(),
                payment.getStatus().toString(),
                payment.getExternalId()
        );
    }
}
