package offers;

import enums.*;

public interface DiscountStrategy {
     DiscountStrategies getDiscountStrategy();
     double applyDiscount(double subtotal);
     default boolean isActive() {
        return getDiscountStrategy() != DiscountStrategies.NO_DISCOUNT;
    }
}
