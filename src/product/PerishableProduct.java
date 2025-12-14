package product;
import enums.*;
import java.time.LocalDate;

public class PerishableProduct extends Product {
    private LocalDate expiryDate;

    public PerishableProduct(String name, Category category, double unitPrice, int stockQuantity, int lowStockQuantityThreshold, LocalDate expiryDate) {
        super(name, category, unitPrice, stockQuantity, lowStockQuantityThreshold);
        setExpiryDate(expiryDate);
    }
    
    public PerishableProduct(int productId, String name, Category category, double unitPrice, int stockQuantity, int lowStockQuantityThreshold, LocalDate expiryDate) {
        super(productId, name, category, unitPrice, stockQuantity, lowStockQuantityThreshold);
        setExpiryDate(expiryDate);
    }

    // -- Methods
    public boolean isExpired() {
        return this.expiryDate.isBefore(LocalDate.now());
    }
    public boolean isNearExpiry() {
        return !isExpired() && expiryDate.isBefore(LocalDate.now().plusDays(4));
    }

    // -- Getters
    @Override
    public String getProductType() { return ProductType.PERISHABLE.name(); }
    public LocalDate getExpiryDate() { return expiryDate; }

   // -- Setters
   public void setExpiryDate(LocalDate newDate) {
       if (newDate == null || newDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("Expiry date must be today or a future date");
       this.expiryDate = newDate;
    }

}


