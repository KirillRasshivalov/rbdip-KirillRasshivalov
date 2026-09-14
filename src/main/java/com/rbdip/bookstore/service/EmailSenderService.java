package com.rbdip.bookstore.service;

import java.math.BigDecimal;

public interface EmailSenderService {

    void sendConfirmationEmail(String customerName, Long orderId, BigDecimal total);

}
