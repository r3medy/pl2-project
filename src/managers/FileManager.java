package managers;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import enums.*;
import offers.*;
import product.*;
import sales.*;
import users.*;

public class FileManager {
    private static final String productsFilePath = "data/products.csv";
    private static final String salesFilePath = "data/sales.csv";
    private static final String usersFilePath = "data/users.csv"; 
    
    public FileManager() {}
    
    public static boolean saveProducts(List<Product> products) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(productsFilePath))) {
            for (Product p : products) {
                String discountData = serializeDiscount(p.getDiscountStrategy());
                if (p instanceof PerishableProduct) {
                    PerishableProduct pp = (PerishableProduct) p;
                    writer.write(p.getProductId() + "," + p.getName() + "," + p.getCategory() + "," +
                                 p.getUnitPrice() + "," + p.getStockQuantity() + "," +
                                 p.getLowStockQuantityThreshold() + ",PERISHABLE," + pp.getExpiryDate() + "," + discountData);
                } else if (p instanceof NonPerishableProduct) {
                    NonPerishableProduct np = (NonPerishableProduct) p;
                    writer.write(p.getProductId() + "," + p.getName() + "," + p.getCategory() + "," +
                                 p.getUnitPrice() + "," + p.getStockQuantity() + "," +
                                 p.getLowStockQuantityThreshold() + ",NON_PERISHABLE," + np.getWarrantyMonths() + "," + discountData);
                }
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public static List<Product> loadProducts() {
        List<Product> products = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(productsFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Product product = parseProductLine(line);
                if (product != null) products.add(product);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return products;
    }

    public static boolean saveUsers(List<User> users) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(usersFilePath))) {
            for (User u : users) {
                writer.write(u.getUserId() + "," + u.getUserType().name() + "," + u.getName() + "," + u.getUsername() + "," + u.getPassword());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(usersFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts[0].trim().toLowerCase().startsWith("userid")) continue;
                int userId = Integer.parseInt(parts[0].trim());
                String role = parts[1].trim();
                String name = parts[2].trim();
                String username = parts[3].trim();
                String password = parts[4].trim();

                switch(role) {
                    case "ADMIN":
                        users.add(new Admin(userId, name, username, password));
                        break;
                    case "INVENTORY":
                        users.add(new Inventory(userId, name, username, password));
                        break;
                    case "MARKETING":
                        users.add(new Marketing(userId, name, username, password));
                        break;
                    case "SALES":
                        users.add(new Sales(userId, name, username, password));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public static boolean saveSales(List<Sale> sales) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(salesFilePath))) {
            for (Sale s : sales) {
                 
                StringBuilder itemsStr = new StringBuilder();
                for (int i = 0; i < s.getSaleItems().size(); i++) {
                    SaleItem item = s.getSaleItems().get(i);
                    itemsStr.append(item.getProduct().getProductId()).append(":").append(item.getQuantity());
                    if (i < s.getSaleItems().size() - 1) {
                        itemsStr.append(";");
                    }
                }
                writer.write(s.getSaleId() + "," + s.getSaleDate() + "," +
                     s.getSubTotal() + "," + s.getDiscountAmount() + "," + s.getTotal() + "," + itemsStr);
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Sale> loadSales() {
        List<Sale> sales = new ArrayList<>();
        List<Product> products = loadProducts();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("M/d/yyyy");
        
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(salesFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || line.trim().toLowerCase().startsWith("id")) continue;
                
                String[] parts = line.split(",");
                if (parts.length < 5) continue;
                
                int saleId = Integer.parseInt(parts[0].trim());
                
                String dateStr = parts[1].trim();
                LocalDate date;
                try {
                    date = LocalDate.parse(dateStr, dateFormatter);
                } catch (java.time.format.DateTimeParseException e) {
                    date = LocalDate.parse(dateStr);
                }

                double storedSubtotal = Double.parseDouble(parts[2].trim());
                double storedDiscount = Double.parseDouble(parts[3].trim());
                double storedTotal = Double.parseDouble(parts[4].trim());
                
                List<SaleItem> items = new ArrayList<>();
                if (parts.length > 5 && parts[5] != null && !parts[5].trim().isEmpty()) {
                    String[] itemParts = parts[5].trim().split(";");
                    for (String itemPart : itemParts) {
                        if (itemPart.trim().isEmpty()) continue;
                        String[] pq = itemPart.split(":");
                        int productId = Integer.parseInt(pq[0].trim());
                        int quantity = Integer.parseInt(pq[1].trim());
                        
                        for (Product p : products) {
                            if (p.getProductId() == productId) {
                                items.add(new SaleItem(p, quantity));
                                break;
                            }
                        }
                    }
                }
                
                Sale sale = new Sale(saleId, date, items, null, storedSubtotal, storedDiscount, storedTotal);
                sales.add(sale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sales;
    }

    public static Sale findSaleById(int saleId) {
        List<Sale> sales = loadSales();
        for (Sale s : sales) {
            if (s.getSaleId() == saleId) return s;
        }
        return null;
    }
  
    public static Product parseProductLine(String line) {
        try {
            if (line == null || line.trim().isEmpty() || line.trim().toLowerCase().startsWith("id")) return null;

            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0].trim());
            String name = parts[1].trim();
            Category category = Category.valueOf(parts[2].trim());
            double unitPrice = Double.parseDouble(parts[3].trim());
            int stock = Integer.parseInt(parts[4].trim());
            int lowStockQuantityThreshold = Integer.parseInt(parts[5].trim());
            String type = parts[6].trim();

            Product product;
            if (ProductType.PERISHABLE.name().equalsIgnoreCase(type)) {
                LocalDate expiry = LocalDate.parse(parts[7].trim());
                product = new PerishableProduct(id, name, category, unitPrice, stock, lowStockQuantityThreshold, expiry);
            } else {
                int warrantyMonths = Integer.parseInt(parts[7].trim());
                product = new NonPerishableProduct(id, name, category, unitPrice, stock, lowStockQuantityThreshold, warrantyMonths);
            }

            if (parts.length > 8) {
                String discountType = parts[8].trim();
                String discountParams = parts.length > 9 ? parts[9].trim() : "";
                DiscountStrategy discount = parseDiscount(discountType, discountParams);
                product.setDiscountStrategy(discount);
            }

            return product;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String serializeDiscount(DiscountStrategy discountStrat) {
        if (discountStrat instanceof BuyXGetYFree) {
            BuyXGetYFree bxgyf = (BuyXGetYFree) discountStrat;
            return "BUY_X_GET_Y_FREE," + bxgyf.getBuyQuantity() + ":" + bxgyf.getFreeQuantity();
        }
        return "NONE,";
    }

    private static DiscountStrategy parseDiscount(String type, String params) {
        if ("BUY_X_GET_Y_FREE".equalsIgnoreCase(type) && !params.isEmpty()) {
            String[] qtyParts = params.split(":");
            if (qtyParts.length == 2) {
                int buyQty = Integer.parseInt(qtyParts[0].trim());
                int freeQty = Integer.parseInt(qtyParts[1].trim());
                return new BuyXGetYFree(buyQty, freeQty);
            }
        }
        return new NoDiscount();
    }
}
