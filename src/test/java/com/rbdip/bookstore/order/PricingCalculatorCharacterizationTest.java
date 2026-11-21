package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingCalculatorCharacterizationTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void appliesVipDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)), "vip", null);

        assertThat(total).isEqualByComparingTo("90.00");
    }

    @Test
    void appliesWholesaleDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)), "wholesale", null);

        assertThat(total).isEqualByComparingTo("85.00");
    }

    @Test
    void appliesVolumeDiscountWhenQuantityAboveTen() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 11)), "regular", null);

        assertThat(total).isEqualByComparingTo("104.50");
    }

    @Test
    void doesNotApplyVolumeDiscountAtExactlyTen() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 10)), "regular", null);

        assertThat(total).isEqualByComparingTo("100.00");
    }

    @Test
    void appliesSave10Coupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("50.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("40.00");
    }

    @Test
    void appliesSave20PercentCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)), "regular", "SAVE20PERCENT");

        assertThat(total).isEqualByComparingTo("80.00");
    }

    @Test
    void clampsNegativeTotalToZeroAfterCoupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("5.00"), 1)), "regular", "SAVE10");

        assertThat(total).isEqualByComparingTo("0.00");
    }

    @Test
    void appliesExtraDiscountWhenTotalAboveThousand() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("600.00"), 2)), "regular", null);

        assertThat(total).isEqualByComparingTo("1176.00");
    }

    @Test
    void doesNotApplyExtraDiscountAtExactlyThousand() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("500.00"), 2)), "regular", null);

        assertThat(total).isEqualByComparingTo("1000.00");
    }

    @Test
    void combinesVipDiscountAndSave10Coupon() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("100.00"), 1)), "vip", "SAVE10");

        assertThat(total).isEqualByComparingTo("80.00");
    }

    @Test
    void treatsUnknownCustomerTypeAsNoCustomerDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)), "unknown", null);

        assertThat(total).isEqualByComparingTo("20.00");
    }

    @Test
    void treatsNullCustomerTypeAsNoCustomerDiscount() {
        BigDecimal total = calculator.calculateOrderTotal(
                List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)), null, null);

        assertThat(total).isEqualByComparingTo("20.00");
    }

    @Test
    void ignoresBlankAndInvalidCouponCodes() {
        assertThat(calculator.calculateOrderTotal(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)), "regular", ""))
                .isEqualByComparingTo("20.00");
        assertThat(calculator.calculateOrderTotal(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)), "regular", "   "))
                .isEqualByComparingTo("20.00");
        assertThat(calculator.calculateOrderTotal(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), 2)), "regular", "INVALID"))
                .isEqualByComparingTo("20.00");
    }

    @Test
    void clampsNegativeLineTotalFromNegativePriceOrQuantity() {
        assertThat(calculator.calculateOrderTotal(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("-5.00"), 3)), "regular", null))
                .isEqualByComparingTo("0.00");
        assertThat(calculator.calculateOrderTotal(
                        List.of(new PricingCalculator.LineItem(new BigDecimal("10.00"), -1)), "regular", null))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void handlesEmptyItemsList() {
        BigDecimal total = calculator.calculateOrderTotal(List.of(), "regular", null);

        assertThat(total).isEqualByComparingTo("0.00");
    }
}
