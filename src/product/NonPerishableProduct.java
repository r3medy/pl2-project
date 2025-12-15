package product;
import enums.*;

public class NonPerishableProduct extends Product {
    private int warrantyMonths;

    public NonPerishableProduct(String name, Category category, double unitPrice, int stockQuantity, int lowStockQuantityThreshold, int warrantyMonths) {
        super(name, category, unitPrice, stockQuantity, lowStockQuantityThreshold);
        setWarrantyMonths(warrantyMonths);
    }

    public NonPerishableProduct(int productId, String name, Category category, double unitPrice, int stockQuantity, int lowStockQuantityThreshold, int warrantyMonths) {
        super(productId, name, category, unitPrice, stockQuantity, lowStockQuantityThreshold);
        setWarrantyMonths(warrantyMonths);
    }

    @Override
    public String getProductType() { return ProductType.NON_PERISHABLE.name(); }
    public int getWarrantyMonths() { return warrantyMonths; }

    public void setWarrantyMonths(int newWarrantyMonths) {
        if(newWarrantyMonths < 0) throw new IllegalArgumentException("Warranty months cannot be negative");
        this.warrantyMonths = newWarrantyMonths;
    }
}
