package offers;

import enums.*;

public class NoDiscount implements DiscountStrategy {
    @Override
    public double applyDiscount(double subtotal) {
        return subtotal;
    }

    @Override
    public String getDiscountStrategy() {
        return enums.DiscountStrategy.NO_DISCOUNT.name();
    }
}
