package com.rbdip.bookstore.dto;

public record CreateReviewRequest(String authorName, Integer rating, String comment) {
}
