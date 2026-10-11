package com.rbdip.bookstore.order;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Модуль расчёта цены заказа. Намеренно почти не покрыт тестами и
 * содержит magic numbers / нечитаемые ветвления скидок - цель для
 * характеризационных тестов (ЛР2) и mutation-testing гейта PIT (ЛР5).
 */
public class PricingCalculator {

    private static final Integer MAX_QUANTITY = 10;
    private static final BigDecimal PRICE_COEFFICIENT = BigDecimal.valueOf(0.95);
    private static final BigDecimal VIP_DISCOUNT_RATE = BigDecimal.valueOf(0.9);
    private static final BigDecimal WHOLESALE_DISCOUNT_RATE = BigDecimal.valueOf(0.85);
    private static final BigDecimal COUPON_SAVE10 = BigDecimal.TEN;
    private static final BigDecimal COUPON_SAVE20PERCENT = BigDecimal.valueOf(0.8);
    private static final BigDecimal MAX_TOTAL_FOR_EXTRA_DISCOUNT = BigDecimal.valueOf(1000);
    private static final BigDecimal MAX_TOTAL_COEFFICIENT = BigDecimal.valueOf(0.98);
    private static final BigDecimal MIN_TOTAL = BigDecimal.ZERO;
    private static final Integer LOWER_ENTRY_THRESHOLD = 0;
    private static final String CUSTOMER_TYPE_VIP = "vip";
    private static final String CUSTOMER_TYPE_WHOLESALE = "wholesale";
    private static final String COUPON_CODE_SAVE10 = "SAVE10";
    private static final String COUPON_CODE_SAVE20_PERCENT = "SAVE20PERCENT";

    public record LineItem(BigDecimal price, int quantity) {
    }

    public BigDecimal calculateOrderTotal(List<LineItem> items, String customerType, String couponCode) {
        BigDecimal total = MIN_TOTAL;

        for (LineItem item : items) {
            BigDecimal linePrice = item.price().multiply(BigDecimal.valueOf(item.quantity()));
            if (item.quantity() > MAX_QUANTITY) {
                linePrice = linePrice.multiply(PRICE_COEFFICIENT);
            }
            total = total.add(linePrice);
        }

        if (CUSTOMER_TYPE_VIP.equals(customerType)) {
            total = total.multiply(VIP_DISCOUNT_RATE);
        } else if (CUSTOMER_TYPE_WHOLESALE.equals(customerType)) {
            total = total.multiply(WHOLESALE_DISCOUNT_RATE);
        }

        if (COUPON_CODE_SAVE10.equals(couponCode)) {
            total = total.subtract(COUPON_SAVE10);
        } else if (COUPON_CODE_SAVE20_PERCENT.equals(couponCode)) {
            total = total.multiply(COUPON_SAVE20PERCENT);
        }

        if (total.compareTo(MIN_TOTAL) < LOWER_ENTRY_THRESHOLD) {
            total = MIN_TOTAL;
        }

        if (total.compareTo(MAX_TOTAL_FOR_EXTRA_DISCOUNT) > LOWER_ENTRY_THRESHOLD) {
            total = total.multiply(MAX_TOTAL_COEFFICIENT);
        }

        return total.setScale(2, RoundingMode.HALF_UP);
    }
}
