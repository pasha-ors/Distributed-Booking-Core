package com.booking.order.service;

import com.booking.order.entity.Order;
import com.booking.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Order createOrder(Long userId) {
        Order order = new Order();
        order.setUserId(userId);

        return orderRepository.save(order);
    }

}
