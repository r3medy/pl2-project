package offers;

import enums.*;

public class FixedDiscount implements DiscountStrategy {
    private double discountAmount;

    public FixedDiscount(double discountAmount) {
        this.discountAmount =Math.max(0,discountAmount);
    }

    @Override
    public double applyDiscount(double subtotal) {
        return Math.min(discountAmount,subtotal);
    }

    @Override
    public DiscountStrategies getDiscountStrategy() {
        return DiscountStrategies.FIXED_DISCOUNT;
    }
}
