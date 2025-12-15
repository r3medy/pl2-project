package sales;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import managers.FileManager;
import offers.*;
import product.Product;

public class Sale {
    private static int idCounter;
    private int saleId;
    private LocalDate saleDate;
    private List<SaleItem> items;
    private DiscountStrategy discountStrategy;
    private double subTotal;
    private double discountAmount;
    private double totalAmount;
    
    public Sale() {
        this(null);
    }

    public Sale(DiscountStrategy discountStrategy) {
        this(++idCounter, LocalDate.now(), new ArrayList<>(), discountStrategy);
    }

    public Sale(int saleId, LocalDate saleDate, List<SaleItem> items, DiscountStrategy discountStrategy) {
        if(saleId <= 0) throw new IllegalArgumentException("Sale ID must be greater than 0");
        if(saleDate == null) throw new IllegalArgumentException("Sale date cannot be null");
        if(items == null) throw new IllegalArgumentException("Items cannot be null");

        List<Sale> existingSales = FileManager.loadSales();
        Sale.idCounter = existingSales.stream()
                .mapToInt(Sale::getSaleId)
                .max()
                .orElse(0);

        this.saleId = saleId;
        this.saleDate = saleDate;
        this.items = items;
        this.discountStrategy = discountStrategy == null ? new NoDiscount() : discountStrategy;
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
        System.out.println("Sale ID    :: " + saleId);
        System.out.println("Date       :: " + saleDate);
        System.out.println("Items      :: ");
        for (SaleItem item : items) System.out.println("    - " + item.getProduct().getName() + " x" + item.getQuantity() + " = " + item.getSaleTotalPrice());
        System.out.println("--------------------------------");
        System.out.println("Subtotal   :: " + subTotal);
        System.out.println("Discount   :: " + discountAmount);
        System.out.println("Total      :: " + totalAmount);
        return "";
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
    public int getSaleId() { return saleId; }

    
    public void processSale() {
        // Decrease stock for all items
        for(SaleItem item : this.items) {
            Product p = item.getProduct();
            int qty = item.getQuantity();
            p.decreaseStock(qty);
        }
        
        // Save the sale
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
