package offers;

import enums.*;

public class NoDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double subtotal) {
        return 0.0;
    }

    @Override
    public DiscountStrategies getDiscountStrategy() {
        return DiscountStrategies.NO_DISCOUNT;
    }
}
