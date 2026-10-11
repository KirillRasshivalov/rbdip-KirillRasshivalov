package com.rbdip.bookstore.service;

import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderServiceImpl implements EmailSenderService {

    private static final Logger log = LoggerFactory.getLogger(EmailSenderServiceImpl.class);

    @Override
    public void sendConfirmationEmail(String customerName, Long orderId, BigDecimal total) {
        log.info("[email] Dear {}, your order #{} for {} has been placed.", customerName, orderId, total);
    }
}
