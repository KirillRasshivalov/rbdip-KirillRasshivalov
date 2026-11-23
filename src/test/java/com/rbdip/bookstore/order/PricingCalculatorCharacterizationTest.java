package com.rbdip.bookstore.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class PricingCalculatorCharacterizationTest {

    private final PricingCalculator calculator = new PricingCalculator();

    @Test
    void calculatesSimpleRegularOrder() {
        assertThat(calculator.calculateOrderTotal(
                        List.of(item("10.00", 2)), "regular", null))
                .isEqualByComparingTo("20.00");
    }

    @Test
    void appliesVipDiscount() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "vip", null))
                .isEqualByComparingTo("90.00");
    }

    @Test
    void appliesWholesaleDiscount() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "wholesale", null))
                .isEqualByComparingTo("85.00");
    }

    @Test
    void vipAndWholesaleAreMutuallyExclusivePreferringVip() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "vip", null))
                .isEqualByComparingTo("90.00");
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "wholesale", null))
                .isEqualByComparingTo("85.00");
    }

    @Test
    void appliesVolumeDiscountWhenQuantityAboveTen() {
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 11)), "regular", null))
                .isEqualByComparingTo("104.50");
    }

    @Test
    void doesNotApplyVolumeDiscountAtExactlyTen() {
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 10)), "regular", null))
                .isEqualByComparingTo("100.00");
    }

    @Test
    void doesNotApplyVolumeDiscountJustBelowThreshold() {
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 9)), "regular", null))
                .isEqualByComparingTo("90.00");
    }

    @Test
    void appliesSave10Coupon() {
        assertThat(calculator.calculateOrderTotal(List.of(item("50.00", 1)), "regular", "SAVE10"))
                .isEqualByComparingTo("40.00");
    }

    @Test
    void appliesSave20PercentCoupon() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "regular", "SAVE20PERCENT"))
                .isEqualByComparingTo("80.00");
    }

    @Test
    void save10AndSave20PercentAreMutuallyExclusivePreferringSave10() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "regular", "SAVE10"))
                .isEqualByComparingTo("90.00");
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "regular", "SAVE20PERCENT"))
                .isEqualByComparingTo("80.00");
    }

    @Test
    void clampsNegativeTotalToZeroAfterCoupon() {
        assertThat(calculator.calculateOrderTotal(List.of(item("5.00", 1)), "regular", "SAVE10"))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void zeroTotalStaysZeroWithoutClampingSideEffects() {
        assertThat(calculator.calculateOrderTotal(List.of(item("0.00", 5)), "regular", null))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void appliesExtraDiscountWhenTotalAboveThousand() {
        assertThat(calculator.calculateOrderTotal(List.of(item("600.00", 2)), "regular", null))
                .isEqualByComparingTo("1176.00");
    }

    @Test
    void doesNotApplyExtraDiscountAtExactlyThousand() {
        assertThat(calculator.calculateOrderTotal(List.of(item("500.00", 2)), "regular", null))
                .isEqualByComparingTo("1000.00");
    }

    @Test
    void doesNotApplyExtraDiscountJustBelowThousand() {
        assertThat(calculator.calculateOrderTotal(List.of(item("999.00", 1)), "regular", null))
                .isEqualByComparingTo("999.00");
    }

    @Test
    void appliesExtraDiscountJustAboveThousand() {
        assertThat(calculator.calculateOrderTotal(List.of(item("1001.00", 1)), "regular", null))
                .isEqualByComparingTo("980.98");
    }

    @Test
    void combinesVipDiscountAndSave10Coupon() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "vip", "SAVE10"))
                .isEqualByComparingTo("80.00");
    }

    @Test
    void combinesWholesaleAndSave20Percent() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "wholesale", "SAVE20PERCENT"))
                .isEqualByComparingTo("68.00");
    }

    @Test
    void combinesVolumeDiscountVipAndSave10() {
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 11)), "vip", "SAVE10"))
                .isEqualByComparingTo("84.05");
    }

    @Test
    void appliesExtraDiscountAfterVipOnLargeOrder() {
        assertThat(calculator.calculateOrderTotal(List.of(item("2000.00", 1)), "vip", null))
                .isEqualByComparingTo("1764.00");
    }

    @Test
    void sumsMultipleLineItemsBeforeDiscounts() {
        assertThat(calculator.calculateOrderTotal(
                        List.of(item("10.00", 2), item("5.00", 3)), "regular", null))
                .isEqualByComparingTo("35.00");
    }

    @Test
    void appliesVolumeDiscountPerLineIndependently() {
        assertThat(calculator.calculateOrderTotal(
                        List.of(item("10.00", 10), item("10.00", 11)), "regular", null))
                .isEqualByComparingTo("204.50");
    }

    @Test
    void treatsUnknownCustomerTypeAsNoCustomerDiscount() {
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 2)), "unknown", null))
                .isEqualByComparingTo("20.00");
    }

    @Test
    void treatsNullCustomerTypeAsNoCustomerDiscount() {
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 2)), null, null))
                .isEqualByComparingTo("20.00");
    }

    @Test
    void ignoresBlankAndInvalidCouponCodes() {
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 2)), "regular", ""))
                .isEqualByComparingTo("20.00");
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 2)), "regular", "   "))
                .isEqualByComparingTo("20.00");
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 2)), "regular", "INVALID"))
                .isEqualByComparingTo("20.00");
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 2)), "regular", null))
                .isEqualByComparingTo("20.00");
    }

    @Test
    void couponCodesAreCaseSensitive() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "regular", "save10"))
                .isEqualByComparingTo("100.00");
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "regular", "Vip"))
                .isEqualByComparingTo("100.00");
    }

    @Test
    void customerTypeIsCaseSensitive() {
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "VIP", null))
                .isEqualByComparingTo("100.00");
        assertThat(calculator.calculateOrderTotal(List.of(item("100.00", 1)), "Wholesale", null))
                .isEqualByComparingTo("100.00");
    }

    @Test
    void clampsNegativeLineTotalFromNegativePriceOrQuantity() {
        assertThat(calculator.calculateOrderTotal(List.of(item("-5.00", 3)), "regular", null))
                .isEqualByComparingTo("0.00");
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", -1)), "regular", null))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void handlesEmptyItemsList() {
        assertThat(calculator.calculateOrderTotal(List.of(), "regular", null))
                .isEqualByComparingTo("0.00");
    }

    @Test
    void roundsHalfUpToTwoDecimalPlaces() {
        // 10 * 11 * 0.95 = 104.5; * 0.85 = 88.825 → 88.83
        assertThat(calculator.calculateOrderTotal(List.of(item("10.00", 11)), "wholesale", null))
                .isEqualByComparingTo("88.83");
    }

    @Test
    void returnsNonNullScaledResult() {
        BigDecimal total = calculator.calculateOrderTotal(List.of(item("1.00", 1)), "regular", null);

        assertThat(total).isNotNull();
        assertThat(total.scale()).isEqualTo(2);
        assertThat(total).isEqualByComparingTo("1.00");
    }

    private static PricingCalculator.LineItem item(String price, int quantity) {
        return new PricingCalculator.LineItem(new BigDecimal(price), quantity);
    }
}
