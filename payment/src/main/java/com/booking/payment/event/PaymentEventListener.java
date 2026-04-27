package com.booking.payment.event;

import com.booking.payment.service.PaymentAsyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventListener {

        private final PaymentAsyncService paymentAsyncService;

        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handle(PaymentCreatedEvent event) {
            paymentAsyncService.processPaymentAsync(event.paymentId());
        }
}
