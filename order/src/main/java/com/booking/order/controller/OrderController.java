package com.booking.order.controller;

import com.booking.order.entity.Order;
import com.booking.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Order createOrder(@RequestParam Long userId){
        return orderService.createOrder(userId);
    }
}
