package com.rbdip.bookstore.service;

import com.rbdip.bookstore.order.OrderService;
import org.springframework.stereotype.Service;

@Service
public class ApiService {

    private final OrderService orderService;

    public ApiService(OrderService orderService) {
        this.orderService = orderService;
    }

    public boolean checkExistingInOrderService() {
        return orderService.verifyOrderExists();
    }
}
