package sales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import managers.FileManager;
import offers.DiscountStrategy;
import offers.NoDiscount;
import product.Product;

public class Sale {
    private static int idCounter = 0;
    private static boolean idCounterInitialized = false;
    
    private int saleId;
    private LocalDate saleDate;
    private List<SaleItem> items;
    private DiscountStrategy discountStrategy;
    
    private double itemsSubtotal;
    private double itemDiscountsTotal;
    private double subTotal;
    private double saleDiscountAmount;
    private double discountAmount;
    private double totalAmount;

    private static void initIdCounterIfNeeded() {
        if (!idCounterInitialized) {
            idCounterInitialized = true;
            List<Sale> existingSales = FileManager.loadSales();
            idCounter = existingSales.stream()
                    .mapToInt(Sale::getSaleId)
                    .max()
                    .orElse(0);
        }
    }
    
    public Sale(List<SaleItem> items, DiscountStrategy discountStrategy) {
        initIdCounterIfNeeded();
        this.saleId = ++idCounter;
        this.saleDate = LocalDate.now();
        this.items = new ArrayList<>(items);
        this.discountStrategy = discountStrategy != null ? discountStrategy : new NoDiscount();
        recalcTotals();
    }

    public Sale(int saleId, LocalDate saleDate, List<SaleItem> items, DiscountStrategy discountStrategy) {
        validateConstructorArgs(saleId, saleDate, items);
        initIdCounterIfNeeded();
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.items = new ArrayList<>(items);
        this.discountStrategy = discountStrategy != null ? discountStrategy : new NoDiscount();
        recalcTotals();
    }

    public Sale(int saleId, LocalDate saleDate, List<SaleItem> items, DiscountStrategy discountStrategy,
                double storedSubTotal, double storedDiscountAmount, double storedTotalAmount) {
        validateConstructorArgs(saleId, saleDate, items);
        initIdCounterIfNeeded();
        this.saleId = saleId;
        this.saleDate = saleDate;
        this.items = new ArrayList<>(items);
        this.discountStrategy = discountStrategy != null ? discountStrategy : new NoDiscount();
        
        this.subTotal = storedSubTotal;
        this.discountAmount = storedDiscountAmount;
        this.totalAmount = storedTotalAmount;
        
        this.itemsSubtotal = calculateItemsSubtotal();
        this.itemDiscountsTotal = calculateItemDiscountsTotal();
        this.saleDiscountAmount = Math.max(0, storedDiscountAmount - itemDiscountsTotal);
    }
    
    private void validateConstructorArgs(int saleId, LocalDate saleDate, List<SaleItem> items) {
        if (saleId <= 0) throw new IllegalArgumentException("Sale ID must be greater than 0");
        if (saleDate == null) throw new IllegalArgumentException("Sale date cannot be null");
        if (items == null) throw new IllegalArgumentException("Items cannot be null");
    }
    
    private double calculateItemsSubtotal() {
        return items.stream()
                .mapToDouble(i -> i.getProduct().getUnitPrice() * i.getQuantity())
                .sum();
    }
    
    private double calculateItemDiscountsTotal() {
        return items.stream()
                .mapToDouble(SaleItem::getItemDiscount)
                .sum();
    }

    public boolean addSaleItem(SaleItem item) {
        if (item == null) return false;
        items.add(item);
        recalcTotals();
        return true;
    }

    public boolean removeSaleItem(SaleItem item) {
        if (item == null) return false;
        boolean removed = items.remove(item);
        if (removed) {
            recalcTotals();
        }
        return removed;
    }

    public boolean removeSaleItemById(int productId) {
        boolean removed = items.removeIf(i -> i.getProduct().getProductId() == productId);
        if (removed) {
            recalcTotals();
        }
        return removed;
    }

    private void recalcTotals() {
        itemsSubtotal = calculateItemsSubtotal();
        itemDiscountsTotal = calculateItemDiscountsTotal();
        subTotal = itemsSubtotal - itemDiscountsTotal;
        saleDiscountAmount = discountStrategy.applyDiscount(subTotal);
        discountAmount = itemDiscountsTotal + saleDiscountAmount;
        totalAmount = subTotal - saleDiscountAmount;
    }

    public void generateReceipt() {
        System.out.println("Sale ID    :: " + saleId);
        System.out.println("Date       :: " + saleDate);
        System.out.println("Items      :: \n");
        for (SaleItem item : items) {
            System.out.printf("   ├─ %-20s x%d = %5.2f$%n", 
                    item.getProduct().getName(), 
                    item.getQuantity(), 
                    item.getSaleTotalPrice());
        }
        System.out.printf("%n- Subtotal   :: %5.2f$%n", subTotal);
        System.out.printf("- Discount   :: %5.2f$%n", discountAmount);
        System.out.printf("- Total      :: %5.2f$%n", totalAmount);
    }

    public LocalDate getSaleDate() { return saleDate; }
    public List<SaleItem> getSaleItems() { return new ArrayList<>(items); }
    public double getSubTotal() { return subTotal; }
    public double getDiscountAmount() { return discountAmount; }
    public int getSaleId() { return saleId; }
    public double getTotal() { return totalAmount; }

    public void processSale() {
        for (SaleItem item : this.items) {
            Product p = item.getProduct();
            int qty = item.getQuantity();
            p.decreaseStock(qty);
        }
        
        List<Sale> allSales = FileManager.loadSales();
        boolean found = false;
        for (int i = 0; i < allSales.size(); i++) {
            if (allSales.get(i).getSaleId() == this.saleId) {
                allSales.set(i, this);
                found = true;
                break;
            }
        }
        if (!found) {
            allSales.add(this);
        }
        
        FileManager.saveSales(allSales);
        FileManager.saveProducts(FileManager.loadProducts());
        generateReceipt();
    }
    
    public void setDiscountStrategy(DiscountStrategy newDiscountStrategy) {
        this.discountStrategy = newDiscountStrategy != null ? newDiscountStrategy : new NoDiscount();
        recalcTotals();
    }

    public void setSaleId(int saleId) { 
        if (saleId <= 0) {
            throw new IllegalArgumentException("Sale ID must be greater than 0");
        }
        this.saleId = saleId; 
    }
}
