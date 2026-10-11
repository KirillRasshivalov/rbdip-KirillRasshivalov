package com.rbdip.bookstore.dto;

import java.util.List;

public record OrderView(Long id, String customerFullName, String status, List<OrderItemView> items) {
}
