package sales;
import product.*;
import managers.*;

public class SaleItem {
    private Product product;
    private int quantity;
    private double saleTotalPrice;
    private InventoryManager inventoryManager = new InventoryManager();

    public SaleItem(int productId, int quantity) {
        if(productId <= 0) throw new IllegalArgumentException("Product ID must be greater than 0");
        if(quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
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
        this.saleTotalPrice = product.getUnitPrice() * quantity;
    }


    // -- Getters
    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }    
    public double getSaleTotalPrice() { return saleTotalPrice; }
    
    // -- Setters
    public void setProduct(Product newProduct) {
        if(newProduct == null) throw new IllegalArgumentException("Product cannot be null");
        this.product = newProduct;
        this.saleTotalPrice = newProduct.getUnitPrice() * this.quantity;
    }

    public void setQuantity(int newQuantity) {
        if(newQuantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
        if(this.product.getStockQuantity() < newQuantity) throw new IllegalArgumentException("Product quantity is not enough");
        this.quantity = newQuantity;
        this.saleTotalPrice = this.product.getUnitPrice() * newQuantity;
    }
}
