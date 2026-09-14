package com.rbdip.bookstore.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    @Override
    public void sendConfirmationEmail(String customerName, Long orderId, BigDecimal total) {
        System.out.printf(
                "[email] Dear %s, your order #%d for %s has been placed.%n", customerName, orderId, total);
    }
}
