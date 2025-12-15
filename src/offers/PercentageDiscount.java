package offers;

public class PercentageDiscount implements DiscountStrategy {
    private double percentage;

    public PercentageDiscount(double percentage) {
        this.percentage = percentage;
    }

    @Override
    public double applyDiscount(double subtotal) {
        return subtotal * (percentage / 100);
    }

    @Override
    public String getDiscountStrategy() {
        return enums.DiscountStrategy.PERCENTAGE_DISCOUNT.name();
    }
}
