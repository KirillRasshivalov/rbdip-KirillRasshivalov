package com.rbdip.bookstore.order;

import com.rbdip.bookstore.dto.OrderCreatedResponse;
import com.rbdip.bookstore.dto.OrderItemView;
import com.rbdip.bookstore.dto.OrderView;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderCreatedResponse createOrder(@RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return new OrderCreatedResponse(order.getId(), order.getStatus());
    }

    @GetMapping("/orders")
    public List<OrderView> listOrders() {
        return orderRepository.findAllWithItems().stream()
                .map(order -> {
                    List<OrderItemView> itemViews = order.getItems().stream()
                            .map(i -> new OrderItemView(i.getProductName(), i.getQuantity()))
                            .toList();
                    return new OrderView(
                            order.getId(),
                            order.getCustomerFullName(),
                            order.getStatus(),
                            itemViews);
                })
                .toList();
    }
}
