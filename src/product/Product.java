package product;
import offers.*;
import java.util.List;
import enums.*;

public abstract class Product {
    private static int idCounter = 0;
    private static boolean idCounterInitialized = false;
    private int productId;
    private String name;
    private Category category;
    private double unitPrice;
    private int stockQuantity;
    private int lowStockQuantityThreshold;
    private DiscountStrategy discountStrategy;


    public static void initializeIdCounter(List<Product> existingProducts) {
        if (!idCounterInitialized && existingProducts != null) {
            idCounter = existingProducts.stream()
                    .mapToInt(Product::getProductId)
                    .max()
                    .orElse(0);
            idCounterInitialized = true;
        }
    }

    public Product(String name, Category category, double unitPrice, int stockQuantity, int lowStockQuantityThreshold) {
        this(++idCounter, name, category, unitPrice, stockQuantity, lowStockQuantityThreshold);
    }

    public Product(int productId, String name, Category category, double unitPrice, int stockQuantity, int lowStockQuantityThreshold) {
        if(productId <= 0) throw new IllegalArgumentException("Product ID must be greater than 0");
        if(name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Product name cannot be empty");
        if(category == null) throw new IllegalArgumentException("Category cannot be empty");
        if(unitPrice < 0) throw new IllegalArgumentException("Unit price cannot be negative");
        if(stockQuantity < 0) throw new IllegalArgumentException("Stock quantity cannot be negative");
        if(lowStockQuantityThreshold < 0) throw new IllegalArgumentException("Low stock quantity threshold cannot be negative");

        if (productId > idCounter) idCounter = productId;

        this.productId = productId;
        this.name = name;
        this.category = category;
        this.unitPrice = unitPrice;
        this.stockQuantity = stockQuantity;
        this.lowStockQuantityThreshold = lowStockQuantityThreshold;
        this.discountStrategy= new NoDiscount();
    }

    public boolean increaseStock(int increment) {
        if(increment <= 0) return false;
        stockQuantity += increment;
        return true;
    }

    public boolean decreaseStock(int decrement) {
        if(decrement <= 0 || stockQuantity < decrement) return false;
        this.stockQuantity -= decrement;
        return true;
    }

    public boolean isLowStock() {
        return this.stockQuantity <= this.lowStockQuantityThreshold;
    }

    public DiscountStrategy getDiscountStrategy() {
        return discountStrategy;
    }
    public void setDiscountStrategy(DiscountStrategy discountStrategy) {
        this.discountStrategy = (discountStrategy == null) ? new NoDiscount() : discountStrategy;
    }
    public boolean isDiscountEligible() {
        return discountStrategy.isActive();
    }
    public double getDiscountedUnitPrice() {
        double discount = discountStrategy.applyDiscount(unitPrice);
        return unitPrice - discount;
    }

    public void productInformation() {
        System.out.println("Product ID      :: " + productId);
        System.out.println("Product Name    :: " + name);
        System.out.println("Product Type    :: " + this.getProductType());
        System.out.println("Unit Price      :: $" + unitPrice);
        System.out.println("Stock Quantity  :: " + stockQuantity);
        System.out.println("Category        :: " + category);
        System.out.println("Low Threshold   :: " + lowStockQuantityThreshold);
        System.out.println(this instanceof PerishableProduct
            ? "Expiry Date     :: " + ((PerishableProduct)this).getExpiryDate()
            : "Warranty Months :: " + ((NonPerishableProduct)this).getWarrantyMonths());
    }

    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getUnitPrice() { return unitPrice; }
    public int getStockQuantity() { return stockQuantity; }
    public Category getCategory() { return category; }
    public int getLowStockQuantityThreshold() { return lowStockQuantityThreshold; }

    public abstract String getProductType();


    public void setName(String name) {
        if(name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Product name cannot be empty");
        this.name = name;
    }
    
    public void setUnitPrice(double newUnitPrice) {
        if(newUnitPrice < 0) throw new IllegalArgumentException("Unit price cannot be negative");
        this.unitPrice = newUnitPrice;
    }

    public void setLowStockQuantityThreshold(int newLowStockQuantityThreshold) {
        if(newLowStockQuantityThreshold < 0) throw new IllegalArgumentException("Low stock quantity threshold cannot be negative");
        this.lowStockQuantityThreshold = newLowStockQuantityThreshold;
    }

    public void setStockQuantity(int newstockQuantity) {
        if(newstockQuantity < 0) throw new IllegalArgumentException("Stock quantity cannot be negative");
        this.stockQuantity = newstockQuantity;
    }

    public void setCategory(Category newcategory) {
        if(newcategory == null) throw new IllegalArgumentException("Category cannot be empty");
        this.category = newcategory;
    }
}
