package sales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import managers.FileManager;
import offers.*;
import product.Product;

public class Sale {
    private static int idCounter = 0;
    private static boolean idCounterInitialized = false;
    private int saleId;
    private LocalDate saleDate;
    private List<SaleItem> items;
    private DiscountStrategy discountStrategy;
    private double Total;
    private double discountAmount;
    private double totalAmount;
    private double itemsSubtotal;
    private double itemDiscountsTotal;
    private double saleDiscount;


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
    
    public Sale() {
        this(null);
    }

    public Sale(DiscountStrategy discountStrategy) {
        initIdCounterIfNeeded();
        this.saleId = ++idCounter;
        this.saleDate = LocalDate.now();
        this.items = new ArrayList<>();
        this.discountStrategy = discountStrategy == null ? new NoDiscount() : discountStrategy;
        recalcTotals();
    }

    public Sale(int saleId, LocalDate saleDate, List<SaleItem> items, DiscountStrategy discountStrategy) {
        if(saleId <= 0) throw new IllegalArgumentException("Sale ID must be greater than 0");
        if(saleDate == null) throw new IllegalArgumentException("Sale date cannot be null");
        if(items == null) throw new IllegalArgumentException("Items cannot be null");

        this.saleId = saleId;
        this.saleDate = saleDate;
        this.items = items;
        this.discountStrategy = discountStrategy == null ? new NoDiscount() : discountStrategy;
        this.Total = 0.0;
        this.discountAmount = 0.0;
        this.totalAmount = 0.0;
        recalcTotals();
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

    private void recalcTotals() {
        itemsSubtotal = items.stream()
                .mapToDouble(i -> i.getProduct().getUnitPrice() * i.getQuantity())
                .sum();

                itemDiscountsTotal = items.stream()
                .mapToDouble(SaleItem::getItemDiscount)
                .sum();

        Total = itemsSubtotal - itemDiscountsTotal;

        saleDiscount = discountStrategy.applyDiscount(Total);

        discountAmount = itemDiscountsTotal + saleDiscount;
        totalAmount = Total - saleDiscount;
    }

    public void generateReceipt() {
        System.out.println("Sale ID    :: " + saleId);
        System.out.println("Date       :: " + saleDate);
        System.out.println("Items      :: \n");
        for (SaleItem item : items) System.out.printf("   ├─ %-20s x%d = %5.2f$%n", item.getProduct().getName(), item.getQuantity(), item.getSaleTotalPrice());
        System.out.printf("%n- Total   :: %5.2f$%n", Total);
        System.out.printf("- Discount   :: %5.2f$%n", discountAmount);
        System.out.printf("- Total      :: %5.2f$%n", totalAmount);
    }

    public LocalDate getSaleDate() { return saleDate; }
    public List<SaleItem> getSaleItems() { return items; }
    public double getTotal() { return Total; }
    public double getDiscountAmount() { return discountAmount; }
    public double getTotalAmount() { return totalAmount; }
    public int getSaleId() { return saleId; }

    
    public void processSale() {
        for(SaleItem item : this.items) {
            Product p = item.getProduct();
            int qty = item.getQuantity();
            p.decreaseStock(qty);
        }
        
        List<Sale> allSales = FileManager.loadSales();
        boolean found = false;
        for(int i = 0; i < allSales.size(); i++) {
            if(allSales.get(i).getSaleId() == this.saleId) {
                allSales.set(i, this);
                found = true;
                break;
            }
        }
        if(!found) allSales.add(this);
        
        FileManager.saveSales(allSales);
        FileManager.saveProducts(FileManager.loadProducts());
        this.generateReceipt();
    }
    
    public void setDiscountStrategy(DiscountStrategy newDiscountStrategy) {
        this.discountStrategy = newDiscountStrategy;
        recalcTotals();
    }

    public void setSaleId(int saleId) { this.saleId = saleId; }
}
