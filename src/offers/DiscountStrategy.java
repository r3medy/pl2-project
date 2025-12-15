package offers;

public interface DiscountStrategy {
    abstract String getDiscountStrategy();
    abstract double applyDiscount(double subtotal);
}
