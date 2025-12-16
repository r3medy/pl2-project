package offers;

import enums.DiscountStrategies;

public class PercentageDiscount implements DiscountStrategy {
    private double percentage;

    public PercentageDiscount(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double applyDiscount(double subtotal) {
        return subtotal - (subtotal * (percentage / 100));
    }

    @Override
    public DiscountStrategies getDiscountStrategy() {
        return DiscountStrategies.PERCENTAGE_DISCOUNT;
    }
}
