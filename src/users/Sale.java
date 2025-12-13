import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private int saleId;
    private LocalDate saleDate;
    private List<SaleItem> items;
    private DiscountStrategy discountStrategy;
    private double subTotal;
    private double discountAmount;
    private double totalAmount;

    
    public Sale() {
        this.saleId = 0;
        this.saleDate = LocalDate.now();
        this.items = new ArrayList<>();
        this.discountStrategy = new NoDiscount(); // default strategy
        this.subTotal = 0.0;
        this.discountAmount = 0.0;
        this.totalAmount = 0.0;
    }

    public boolean addSaleItem(SaleItem item) {
        boolean added = items.add(item);
        recalcTotals();
        return added;
    }

  
    public boolean removeSaleItem(SaleItem item) {
        boolean removed = items.remove(item);
        recalcTotals();
        return removed;
    }

  
    public boolean removeSaleItemById(int productId) {
        boolean removed = items.removeIf(i -> i.getProduct().getProductId() == productId);
        recalcTotals();
        return removed;
    }

  
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sale ID: ").append(saleId).append("\n");
        sb.append("Date: ").append(saleDate).append("\n");
        sb.append("Items:\n");
        for (SaleItem item : items) {
            sb.append("- ").append(item.getProduct().getName())
              .append(" x").append(item.getQuantity())
              .append(" = ").append(item.getSaleTotalPrice()).append("\n");
        }
        sb.append("Subtotal: ").append(subTotal).append("\n");
        sb.append("Discount: ").append(discountAmount).append("\n");
        sb.append("Total: ").append(totalAmount).append("\n");
        return sb.toString();
    }

    
    private void recalcTotals() {
        subTotal = items.stream().mapToDouble(SaleItem::getSaleTotalPrice).sum();
        discountAmount = discountStrategy.applyDiscount(subTotal);
        totalAmount = subTotal - discountAmount;
    }

    public LocalDate getSaleDate() { return saleDate; }
    public List<SaleItem> getSaleItems() { return items; }
    public double getSubTotal() { return subTotal; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTotalAmount() { return totalAmount; }

 
    public void setDiscountStrategy(DiscountStrategy newDiscountStrategy) {
        this.discountStrategy = newDiscountStrategy;
        recalcTotals();
    }

   
    public int getSaleId() { return saleId; }
    public void setSaleId(int saleId) { this.saleId = saleId; }
}
