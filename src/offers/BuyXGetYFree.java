package offers;

public class BuyXGetYFree implements DiscountStrategy {
    private int buyQuantity;
    private int freeQuantity;

    public BuyXGetYFree(int buyQuantity , int freeQuantity) {
        this.buyQuantity = buyQuantity;
        this.freeQuantity = freeQuantity;
    }

    @Override
    public double applyDiscount(double subtotal) {
        int totalQuantity = buyQuantity + freeQuantity;
        double discountAmount = subtotal * ((double) freeQuantity / totalQuantity);
        return discountAmount;
    }

    @Override
    public String getDiscountStrategy() {
        return enums.DiscountStrategy.BUY_X_GET_Y_FREE.name();
    }
}
