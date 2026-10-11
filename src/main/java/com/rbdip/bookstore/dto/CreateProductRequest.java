package com.rbdip.bookstore.dto;

import java.math.BigDecimal;

public record CreateProductRequest(String name, BigDecimal price, String description) {
}
