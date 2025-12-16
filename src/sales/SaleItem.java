package sales;
import product.*;
import managers.*;

public class SaleItem {
    private Product product;
    private int quantity;
    private double itemSubtotal;
    private double itemDiscount;
    private double saleTotalPrice;

    public SaleItem(int productId, int quantity) {
        if(productId <= 0) throw new IllegalArgumentException("Product ID must be greater than 0");
        if(quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
        InventoryManager inventoryManager = new InventoryManager();
        Product product = inventoryManager.findProductById(productId);
        if(product == null) throw new IllegalArgumentException("Product not found");
        if(product.getStockQuantity() < quantity) throw new IllegalArgumentException("Product quantity is not enough");

        this.product = product;
        this.quantity = quantity;
        this.saleTotalPrice = product.getUnitPrice() * quantity;
    }
    
    public SaleItem(Product product, int quantity) {
        if(product == null) throw new IllegalArgumentException("Product cannot be null");
        if(quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
        if(product.getStockQuantity() < quantity) throw new IllegalArgumentException("Product quantity is not enough");

        this.product = product;
        this.quantity = quantity;
        recalculateTotals();
    }

    private void recalculateTotals() {
        itemSubtotal = product.getUnitPrice() * quantity;
        itemDiscount = product.getDiscountStrategy().applyDiscount(itemSubtotal);
        saleTotalPrice = itemSubtotal - itemDiscount;
    }


    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getItemSubtotal() { return itemSubtotal; }
    public double getItemDiscount() { return itemDiscount; }
    public double getSaleTotalPrice() { return saleTotalPrice; }

    public void setProduct(Product newProduct) {
        if(newProduct == null) throw new IllegalArgumentException("Product cannot be null");
        this.product = newProduct;
        recalculateTotals();
    }

    public void setQuantity(int newQuantity) {
        if(newQuantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
        if(this.product.getStockQuantity() < newQuantity) throw new IllegalArgumentException("Product quantity is not enough");
        this.quantity = newQuantity;
        recalculateTotals();
    }
}
