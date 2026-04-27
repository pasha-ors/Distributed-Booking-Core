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
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentProviderClient paymentProviderClient;

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Payment payment = new Payment();

        payment.setOrderId(request.orderId());
        payment.setAmount(request.amount());
        payment.setCurrency(request.currency());

        payment = paymentRepository.save(payment);

        payment.setStatus(PaymentStatus.PROCESSING);

        paymentRepository.save(payment);

        try {
            PaymentProviderResponse response = paymentProviderClient.charge(payment);

            if (response.success()) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setExternalId(response.externalId());
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason(response.errorMessage());
            }

        }catch (Exception ex){
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(ex.getMessage());
        }

        payment = paymentRepository.save(payment);

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
