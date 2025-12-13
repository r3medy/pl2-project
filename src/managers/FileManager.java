package managers;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.time.LocalDate;

public class FileManager {
    private String productsFilePath;
    private String salesFilePath;

    
    public FileManager(String productsFilePath, String salesFilePath) {
        this.productsFilePath = productsFilePath;
        this.salesFilePath = salesFilePath;
    }

    
    public boolean saveProducts(List<Product> products) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(productsFilePath))) {
            for (Product p : products) {
             
                if (p instanceof PerishableProduct) {
                    PerishableProduct pp = (PerishableProduct) p;
                    writer.write(p.getProductId() + "," + p.getName() + "," + p.getCategory() + "," +
                                 p.getUnitPrice() + "," + p.getStockQuantity() + "," +
                                 p.getLowStockThreshold() + ",Perishable," + pp.getExpiryDate());
                } else if (p instanceof NonPerishableProduct) {
                    NonPerishableProduct np = (NonPerishableProduct) p;
                    writer.write(p.getProductId() + "," + p.getName() + "," + p.getCategory() + "," +
                                 p.getUnitPrice() + "," + p.getStockQuantity() + "," +
                                 p.getLowStockThreshold() + ",NonPerishable," + np.getWarrantyMonths());
                }
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Product> loadProducts() {
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

    
    public boolean saveSales(List<Sale> sales) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(salesFilePath))) {
            for (Sale s : sales) {
               
                writer.write(s.getSaleId() + "," + s.getSaleDate() + "," +
                             s.getSubTotal() + "," + s.getDiscountAmount() + "," + s.getTotalAmount());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    public List<Sale> loadSales() {
        List<Sale> sales = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(salesFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int saleId = Integer.parseInt(parts[0]);
                LocalDate date = LocalDate.parse(parts[1]);
                double subTotal = Double.parseDouble(parts[2]);
                double discount = Double.parseDouble(parts[3]);
                double total = Double.parseDouble(parts[4]);

                Sale sale = new Sale();
                sale.setSaleId(saleId);
                sale.setSaleDate(date);
                sale.setSubTotal(subTotal);
                sale.setDiscountAmount(discount);
                sale.setTotalAmount(total);
                sales.add(sale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sales;
    }

  
    public Product parseProductLine(String line) {
        try {
            String[] parts = line.split(",");
            int id = Integer.parseInt(parts[0]);
            String name = parts[1];
            Category category = Category.valueOf(parts[2]);
            double unitPrice = Double.parseDouble(parts[3]);
            int stock = Integer.parseInt(parts[4]);
            int lowStock = Integer.parseInt(parts[5]);
            String type = parts[6];

            if ("Perishable".equalsIgnoreCase(type)) {
                LocalDate expiry = LocalDate.parse(parts[7]);
                return new PerishableProduct(id, name, category, unitPrice, stock, lowStock, expiry);
            } else {
                int warrantyMonths = Integer.parseInt(parts[7]);
                return new NonPerishableProduct(id, name, category, unitPrice, stock, lowStock, warrantyMonths);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
