package offers;

import enums.*;

public interface DiscountStrategy {
    abstract DiscountStrategies getDiscountStrategy();
    abstract double applyDiscount(double subtotal);
}
