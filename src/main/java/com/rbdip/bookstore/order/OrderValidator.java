package com.rbdip.bookstore.order;

import org.springframework.stereotype.Component;

@Component
public class OrderValidator {

    public void validate(CreateOrderRequest request) {
        requireCustomerFullName(request);
        requireCustomerAddress(request);
        requireItems(request);
    }

    public void checkQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
    }

    private void requireCustomerFullName(CreateOrderRequest request) {
        if (request.customerFullName() == null || request.customerFullName().isBlank()) {
            throw new IllegalArgumentException("customerFullName is required");
        }
    }

    private void requireCustomerAddress(CreateOrderRequest request) {
        if (request.customerAddress() == null || request.customerAddress().isBlank()) {
            throw new IllegalArgumentException("customerAddress is required");
        }
    }

    private void requireItems(CreateOrderRequest request) {
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one item");
        }
    }
}
