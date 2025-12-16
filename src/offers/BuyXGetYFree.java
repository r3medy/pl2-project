package offers;

import enums.*;

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
    public DiscountStrategies getDiscountStrategy() {
        return DiscountStrategies.BUY_X_GET_Y_FREE;
    }

    public int getBuyQuantity() { return buyQuantity; }
    public int getFreeQuantity() { return freeQuantity; }
}
