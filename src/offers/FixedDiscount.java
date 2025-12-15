package offers;

import enums.DiscountStrategies;

public class FixedDiscount implements DiscountStrategy {
    private double discountAmount;

    public FixedDiscount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public double applyDiscount(double subtotal) {
        return discountAmount;
    }

    @Override
    public DiscountStrategies getDiscountStrategy() {
        return DiscountStrategies.FIXED_DISCOUNT;
    }
}
