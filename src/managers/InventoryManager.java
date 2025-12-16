package managers;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import product.*;
import sales.*;

public class InventoryManager {
    private List<Product> products;
    private List<Sale> sales;

    public InventoryManager() {
        this.products = FileManager.loadProducts();
        this.sales = FileManager.loadSales();
    }

    public boolean addProduct(Product product) {
        boolean isAdded = products.add(product);
        if(isAdded) return FileManager.saveProducts(products);
        return false;
    }

    public boolean removeProduct(Product product) {
        boolean isRemoved = products.remove(product);
        if(isRemoved) return FileManager.saveProducts(products);
        return false;
    }

    public boolean removeProductById(int productId) {
        Product p = this.findProductById(productId);
        if(p == null) return false;
        return this.removeProduct(p);
    }

    public Product findProductById(int productId) {
        for(Product p : products) {
            if(p.getProductId() == productId) return p;
        }
        return null;
    }

    public Product findProductByName(String productName) {
        for(Product p : products) {
            if(p.getName().toLowerCase().contains(productName.toLowerCase())) return p;
        }
        return null;
    }

    public List<Product> listProductsByCategory(enums.Category category) {
        List<Product> productsByCategory = new ArrayList<>();
        if(category == null) return productsByCategory;
        if(products.isEmpty()) return productsByCategory;

        for(Product p : products) {
            if(p.getCategory() == category) productsByCategory.add(p);
        }
        return productsByCategory;
    }

    public boolean updateProductStock(Product product, int newStock) {
        if(product == null) return false;
        if(newStock < 0) return false;
        product.setStockQuantity(newStock);
        return FileManager.saveProducts(products);
    }

    public List<Product> listTopSellingProducts(int topN) {
        if (topN <= 0) return new ArrayList<>();
        Map<Integer, Integer> productSales = getAllProductSales();

        List<Product> allProducts = new ArrayList<>(products);
        allProducts.sort((a,b) -> Integer.compare(
            productSales.getOrDefault(b.getProductId(), 0),
            productSales.getOrDefault(a.getProductId(), 0))
        );

        int limit = Math.min(topN, allProducts.size());
        return new ArrayList<>(allProducts.subList(0, limit));
    }

    public List<Product> listLeastSellingProducts(int leastN) {
        if (leastN <= 0) return new ArrayList<>();
        Map<Integer, Integer> productSales = getAllProductSales();

        List<Product> allProducts = new ArrayList<>(products);
        allProducts.sort((a,b) -> Integer.compare(
            productSales.getOrDefault(a.getProductId(), 0),
            productSales.getOrDefault(b.getProductId(), 0))
        );

        int limit = Math.min(leastN, allProducts.size());
        return new ArrayList<>(allProducts.subList(0, limit));
    }

    public List<Product> listLowStockProducts() {
        List<Product> lowStockProducts = new ArrayList<>();
        for(Product p : products) {
            if(p.isLowStock()) lowStockProducts.add(p);
        }
        return lowStockProducts;
    }

    public List<Product> listExpiredProducts() {
        List<Product> expiredProducts = new ArrayList<>();
        for(Product p : products) {
            if(p instanceof PerishableProduct && ((PerishableProduct)p).isExpired()) expiredProducts.add(p);
        }
        return expiredProducts;
    }
    public List<Product> listNearExpiryProducts() {
        List<Product> nearExpiryProducts = new ArrayList<>();
        for(Product p : products) {
            if(p instanceof PerishableProduct && ((PerishableProduct)p).isNearExpiry()) nearExpiryProducts.add(p);
        }
        return nearExpiryProducts;
    }

    public List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public List<Sale> getSales() {
        return new ArrayList<>(sales);
    }

    private Map<Integer, Integer> getAllProductSales() {
        Map<Integer, Integer> productSales = new HashMap<>();
        if(this.sales == null) return productSales;
        for(Sale s : this.sales) {
            for(SaleItem item : s.getSaleItems()) {
                if(item == null || item.getProduct() == null) continue;
                productSales.put(item.getProduct().getProductId(), productSales.getOrDefault(item.getProduct().getProductId(), 0) + item.getQuantity());
            }
        }
        return productSales;
    }

    public int getProductSalesCount(int productId) {
        return getAllProductSales().getOrDefault(productId, 0);
    }
}
