package com.booking.payment.controller;

import com.booking.payment.dto.PaymentRequest;
import com.booking.payment.dto.PaymentResponse;
import com.booking.payment.entity.Payment;
import com.booking.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse process(@RequestBody PaymentRequest request) {
        return paymentService.processPayment(request);
    }
}
