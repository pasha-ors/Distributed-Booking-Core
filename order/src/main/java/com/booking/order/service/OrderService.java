package com.booking.order.service;

import com.booking.order.dto.PaymentResponse;
import com.booking.order.entity.Order;
import com.booking.order.entity.OrderStatus;
import com.booking.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final RestTemplate restTemplate;

    public Order createOrder(Long userId) {
        Order order = new Order();
        order.setUserId(userId);

        order = orderRepository.save(order);

        String url = "http://localhost:8082/payments?orderId=" + order.getId();

        PaymentResponse response = restTemplate.postForObject(url, null, PaymentResponse.class);

        if (response != null && "SUCCESS".equals(response.status())) {
            order.setStatus(OrderStatus.CONFIRMED);
        }else{
            order.setStatus(OrderStatus.CANCELLED);
        }

        return orderRepository.save(order);

    }

}
