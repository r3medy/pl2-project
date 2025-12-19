package ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;

import enums.*;
import managers.*;
import offers.*;
import users.*;
import product.*;
import sales.*;

public class ConsoleUI {
    private final UsersManager usersManager = new UsersManager();
    private final InventoryManager inventoryManager = new InventoryManager();
    private Terminal terminal;
    private LineReader lineReader;

    public ConsoleUI() {
        try {
            terminal = TerminalBuilder.terminal();
            lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void start(String errorMessage) {
        clear();
        printTitle();

        if(errorMessage != null) {
            terminal.writer().println((new AttributedString(errorMessage, AttributedStyle.BOLD.foreground(AttributedStyle.RED))).toAnsi() + "\r");
            terminal.writer().flush();
        }

        String username = lineReader.readLine("Enter your Username :: ");
        User user = usersManager.findUserByUsername(username);
        if(user == null) {
            start("Invalid username");
            return;
        }

        String password = lineReader.readLine("Enter your Password :: ", '*'); 
        if(!user.verifyPassword(password)) {
            start("Invalid password");
            return;
        }

        switch(user.getUserType()) {
            case ADMIN:
                this.displayAdminMenu();
                break;
            case INVENTORY:
                this.displayInventoryMenu();
                break;
            case MARKETING:
                this.displayMarketingMenu();
                break;
            case SALES:
                this.displaySalesMenu();
                break;
        }
    }

    public void displayAdminMenu() {
        String[] titles = {
            "View Users",
            "Add User",
            "Remove User",
            "────",
            "View Products",
            "Add Product",
            "Remove Product",
            "Set Product Discount",
            "────",
            "Logout",
            "Exit"
        };

        Runnable[] actions = {
            () -> this.viewUsers(),
            () -> this.addUser(null),
            () -> this.deleteUser(null),
            null,
            () -> this.viewProducts(),
            () -> this.addProduct(null),
            () -> this.deleteProduct(null),
            () -> this.setProductDiscount(null),
            null,
            () -> this.start(null),
            this::exit
        };

        this.displayMenu(titles, actions, null, null);
    }

    public void displayInventoryMenu() {
        String[] titles = {
            "View All Products",
            "Search Products By ID",
            "Search Products By Name",
            "View Near Expiry Products",
            "View Expired Products",
            "View Low Stock Products",
            "────",
            "View Products by Category",
            "────",
            "Logout",
            "Exit"
        };

        Runnable[] actions = {
            () -> this.viewProducts(),
            () -> this.searchProductsById(null),
            () -> this.searchProductsByName(null),
            () -> this.viewNearExpiryProducts(),
            () -> this.viewExpiredProducts(),
            () -> this.viewLowStockProducts(),
            null,
            null,
            null,
            () -> this.start(null),
            () -> this.exit()
        };

        String[][] subMenuTitles = {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            {
                "View Foods",
                "View Drinks",
                "View Electronics",
                "View Cleaning Products",
                "View Other Products",
                "Back"
            },
            null,
            null,
            null
        };

        Runnable[][] subMenuActions = {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            {
                () -> this.viewProductsByCategory(Category.FOOD),
                () -> this.viewProductsByCategory(Category.DRINKS),
                () -> this.viewProductsByCategory(Category.ELECTRONICS),
                () -> this.viewProductsByCategory(Category.CLEANING),
                () -> this.viewProductsByCategory(Category.OTHER),
                null
            },
            null,
            null,
            null
        };

        this.displayMenu(titles, actions, subMenuTitles, subMenuActions);
    }
    
    public void displayMarketingMenu() {
        String[] titles = {
            "View Products",
            "Set Product Discount",
            "────",
            "Logout",
            "Exit"
        };

        Runnable[] actions = {
            () -> this.viewProducts(),
            () -> this.setProductDiscount(null),
            null,
            () -> this.start(null),
            this::exit
        };

        this.displayMenu(titles, actions, null, null);
    }
    
    public void displaySalesMenu() {
        String[] titles = {
            "View Sales",
            "View Sale Details By ID",
            "New Sale",
            "────",
            "Sales Summary",
            "View Top Selling Products",
            "View Least Selling Products",
            "────",
            "Logout",
            "Exit" 
        };

        Runnable[] actions = {
            () -> this.viewSales(),
            () -> this.viewSaleDetailsById(null),
            () -> this.newSale(null),
            null,
            () -> this.viewSalesSummary(),
            () -> this.viewLeastOrTopSellingProducts(10, true),
            () -> this.viewLeastOrTopSellingProducts(10, false),
            null,
            () -> this.start(null),
            this::exit
        };

        this.displayMenu(titles, actions, null, null);
    }

    public void displayProductsMenu() {}
    public void displayReportsMenu() {}
    
    public void exit() {
        try {
            terminal.writer().println("\nGoodbye!");
            terminal.writer().flush();
            terminal.close();
        } catch(Exception e){
        } finally {
            System.exit(0);
        }
    }

    private void displayMenu(String[] titles, Runnable[] actions, String[][] subMenuTitles, Runnable[][] subMenuActions) {
        try {
            int selectedIdx = 0;
            int subMenuSelectedIdx = 0;
            boolean inSubmenu = false;
            
            while (true) {
                clear();
                printTitle();
                
                for (int i = 0; i < titles.length; i++) {
                    terminal.writer().print(getOptionStyle(titles[i], i == selectedIdx, false).toAnsi() + "\r\n");

                    if (i == selectedIdx && inSubmenu && subMenuTitles[i] != null)
                        for (int j = 0; j < subMenuTitles[i].length; j++)
                            terminal.writer().print(getOptionStyle(subMenuTitles[i][j], j == subMenuSelectedIdx, true).toAnsi() + "\r\n");
                }
                
                terminal.writer().println("\nUse ↑↓ arrow keys to navigate, and ⏎ Enter to select\r");
                terminal.writer().flush();

                int initialKey = terminal.reader().read();

                if (initialKey == 27) {
                    int secondKey = terminal.reader().read();
                    if (secondKey == 91 || secondKey == 79) {
                        int arrowDir = terminal.reader().read();
                        switch (arrowDir) {
                            case 65:
                                if (inSubmenu) subMenuSelectedIdx = Math.max(0, subMenuSelectedIdx - 1);
                                else selectedIdx = Math.max(0, selectedIdx - 1);
                                break;
                            case 66:
                                if (inSubmenu) subMenuSelectedIdx = Math.min(subMenuTitles[selectedIdx].length - 1, subMenuSelectedIdx + 1);
                                else selectedIdx = Math.min(titles.length - 1, selectedIdx + 1);
                                break;
                        }
                    }
                } else if (initialKey == 10 || initialKey == 13) {
                    if (inSubmenu) {
                        if (subMenuActions[selectedIdx] != null && subMenuActions[selectedIdx][subMenuSelectedIdx] != null) {
                            subMenuActions[selectedIdx][subMenuSelectedIdx].run();
                        }
                        inSubmenu = false;
                        subMenuSelectedIdx = 0;
                    } else {
                        if (subMenuTitles != null && subMenuTitles[selectedIdx] != null) {
                            inSubmenu = true;
                            subMenuSelectedIdx = 0;
                        } else if (actions[selectedIdx] != null) {
                            actions[selectedIdx].run();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AttributedString getOptionStyle(String optionText, boolean isSelected, boolean isSubmenu) {
        return isSelected ?
            new AttributedString((isSubmenu ? "  ├─  " : "→ ") + optionText, AttributedStyle.DEFAULT.foreground(isSubmenu ? AttributedStyle.YELLOW : AttributedStyle.CYAN)) :
            new AttributedString((isSubmenu ? "  │  " : " ") + optionText);
    }

    private void printTitle() {
        AttributedString title = new AttributedString(" ─ Hypermarket Management System ───", AttributedStyle.BOLD.foreground(AttributedStyle.BLUE));
        terminal.writer().println(title.toAnsi() + "\r\n");
    }

    private void clear() {
        terminal.puts(InfoCmp.Capability.clear_screen);
        terminal.writer().flush();
    }

    private void waitForEnterKey() {
        terminal.writer().println("\nTo go back to the main menu, press ⏎ Enter");
        terminal.writer().flush();
        if(lineReader != null) lineReader.readLine();
    }

    private void displayErrorMessage(String errorMessage) {
        terminal.writer().println((new AttributedString(errorMessage, AttributedStyle.BOLD.foreground(AttributedStyle.RED))).toAnsi() + "\r");
        terminal.writer().flush();
    }

    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isDouble(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void viewUsers() {
        clear();
        printTitle();
        terminal.writer().printf("%5s %20s %20s %20s\n\n", "ID", "Name", "Username", "Type");
        for(User u : usersManager.getUsers()) {
            terminal.writer().printf("%5d %20s %20s %20s\n", u.getUserId(), u.getName(), u.getUsername(), u.getUserType());
        }
        waitForEnterKey();
    }
    
    private void viewProducts() {
        clear();
        printTitle();
        terminal.writer().printf("%4s %25s %8s %20s %10s %12s %10s %20s\n\n", "ID", "Name", "Price", "Discount", "Stock Qty", "Category", "Low Stock?", "Expiry/Warranty");
        for(Product p : inventoryManager.getProducts()) {
            terminal.writer().printf("%4d %25s %8s %20s %10s %12s %10s %20s\n",
                p.getProductId(),
                p.getName(),
                p.getPriceDisplay(),
                p.getDiscountDisplay(),
                p.getStockQuantity(),
                p.getCategory(),
                p.getLowStockDisplay(),
                p.getExpiryOrWarrantyDisplay()
            );
        }

        waitForEnterKey();
    }

    private void viewNearExpiryProducts() {
        clear();
        printTitle();
        terminal.writer().printf("%4s %25s %8s %10s %12s %12s %12s\n\n", "ID", "Name", "Price", "Stock Qty", "Category", "Near Expiry?", "Expiry Date");
        for(Product p : inventoryManager.listNearExpiryProducts()) {
            terminal.writer().printf("%4d %25s %8s %10s %12s %12s %12s\n",
                p.getProductId(),
                p.getName(),
                p.getPriceDisplay(),
                p.getStockQuantity(),
                p.getCategory(),
                ((PerishableProduct)p).isNearExpiry() ? "Yes" : "No",
                ((PerishableProduct)p).getExpiryDate()
            );
        }
        waitForEnterKey();
    }

    private void viewExpiredProducts() {
        clear();
        printTitle();
        terminal.writer().printf("%4s %25s %8s %10s %12s %10s %12s\n\n", "ID", "Name", "Price", "Stock Qty", "Category", "Expired?", "Expiry Date");
        for(Product p : inventoryManager.listExpiredProducts()) {
            if (!(p instanceof PerishableProduct)) continue;
                terminal.writer().printf("%4d %25s %8s %10s %12s %10s %12s\n",
                p.getProductId(),
                p.getName(),
                p.getPriceDisplay(),
                p.getStockQuantity(),
                p.getCategory(),
                ((PerishableProduct)p).isExpired() ? "Yes" : "No",
                ((PerishableProduct)p).getExpiryDate()
            );
        }
        waitForEnterKey();
    }

    private void viewLowStockProducts() {
        clear();
        printTitle();
        terminal.writer().printf("%4s %25s %8s %10s %12s %10s\n\n", "ID", "Name", "Price", "Stock Qty", "Category", "Low Stock?");
        for(Product p : inventoryManager.listLowStockProducts()) {
            if (!(p instanceof PerishableProduct)) continue;
                terminal.writer().printf("%4d %25s %8s %10s %12s %10s\n",
                p.getProductId(),
                p.getName(),
                p.getPriceDisplay(),
                p.getStockQuantity(),
                p.getCategory(),
                p.getLowStockDisplay()
            );
        }
        waitForEnterKey();
    }
    
    private void viewProductsByCategory(Category category) {
        clear();
        printTitle();
        terminal.writer().printf("%4s %25s %8s %10s %12s %10s %20s\n\n", "ID", "Name", "Price", "Stock Qty", "Category", "Low Stock?", "Expiry/Warranty");
        for(Product p : inventoryManager.listProductsByCategory(category)) {
            terminal.writer().printf("%4d %25s %8s %10s %12s %10s %20s\n",
                p.getProductId(),
                p.getName(),
                p.getPriceDisplay(),
                p.getStockQuantity(),
                p.getCategory(),
                p.getLowStockDisplay(),
                p.getExpiryOrWarrantyDisplay()
            );
        }
        waitForEnterKey();
    }

    private void viewSales() {
        clear();
        printTitle();
        terminal.writer().printf("%10s %15s %15s %15s %15s\n\n", "SaleID", "Date", "Subtotal", "Discount", "Total");
        for(Sale s : FileManager.loadSales()) {
            terminal.writer().printf("%10d %15s %15.2f$ %15.2f$ %15.2f$\n",
                s.getSaleId(),
                s.getSaleDate(),
                s.getSubTotal(),
                s.getDiscountAmount(),
                s.getTotal()
            );
        }
        waitForEnterKey();
    }

    private void viewLeastOrTopSellingProducts(int limit, boolean isTop) {
        clear();
        printTitle();
        int ctr=0;
        terminal.writer().printf("%4s %4s %25s %12s %10s %10s %20s %12s %10s %18s\n\n", "No.", "ID", "Name", "Units Sold", "Price", "Stock Qty", "Discount", "Category", "Low Stock?", "Expiry/Warranty");
        for(Product p : (isTop ? inventoryManager.listTopSellingProducts(limit) : inventoryManager.listLeastSellingProducts(limit))) {
            ctr++;
            terminal.writer().printf("%4s %4d %25s %12d %10s %10s %20s %12s %10s %18s\n",
                ctr == 1 ? "1st" : ctr == 2 ? "2nd" : ctr == 3 ? "3rd" : ctr + "th",
                p.getProductId(),
                p.getName(),
                inventoryManager.getProductSalesCount(p.getProductId()),
                p.getPriceDisplay(),
                p.getStockQuantity(),
                p.getDiscountDisplay(),
                p.getCategory(),
                p.getLowStockDisplay(),
                p.getExpiryOrWarrantyDisplay()
            );
        }
        waitForEnterKey();
    }

    private void searchProductsById(String errorMessage) {
        clear();
        printTitle();
        
        if(errorMessage != null) displayErrorMessage(errorMessage);

        String productId = lineReader.readLine("Enter the product ID :: ");
        Product product = inventoryManager.findProductById(Integer.parseInt(productId));
        if(product == null) {
            searchProductsById("Invalid product ID");
            return;
        }

        terminal.writer().printf("%5s %20s %10s %15s %15s %10s %20s\n\n", "ID", "Name", "Price", "Stock Qty", "Category", "Low Stock?", "Expiry/Warranty");
        terminal.writer().printf("%5d %20s %10s %15s %15s %10s %20s\n",
            product.getProductId(),
            product.getName(),
            product.getPriceDisplay(),
            product.getStockQuantity(),
            product.getCategory(),
            product.getLowStockDisplay(),
            product.getExpiryOrWarrantyDisplay()
        );

        waitForEnterKey();
    }

    private void searchProductsByName(String errorMessage) {
        clear();
        printTitle();
        
        if(errorMessage != null) displayErrorMessage(errorMessage);

        String productName = lineReader.readLine("Enter the product name :: ");
        Product product = inventoryManager.findProductByName(productName);
        if(product == null) {
            searchProductsByName("Invalid product name");
            return;
        }

        terminal.writer().printf("%5s %20s %10s %15s %15s %10s %20s\n\n", "ID", "Name", "Price", "Stock Qty", "Category", "Low Stock?", "Expiry/Warranty");
        terminal.writer().printf("%5d %20s %10s %15s %15s %10s %20s\n",
            product.getProductId(),
            product.getName(),
            product.getPriceDisplay(),
            product.getStockQuantity(),
            product.getCategory(),
            product.getLowStockDisplay(),
            product.getExpiryOrWarrantyDisplay()
        );

        waitForEnterKey();
    }

    private void viewSaleDetailsById(String errorMessage) {
        clear();
        printTitle();
        if(errorMessage != null) displayErrorMessage(errorMessage);

        String saleId = lineReader.readLine("Enter the sale ID :: ");
        Sale sale = FileManager.findSaleById(Integer.parseInt(saleId));
        if(sale == null) {
            viewSaleDetailsById("Invalid sale ID");
            return;
        }

        sale.generateReceipt();
        waitForEnterKey();
    }

    private void addUser(String errorMessage) {
        clear();
        printTitle();

        if(errorMessage != null) displayErrorMessage(errorMessage);
        
        String name = lineReader.readLine("Enter the full name :: ");
        if(name.isEmpty() || name.length() < 6) { addUser("Invalid user name, must be more than 6 characters"); return; }

        String username = lineReader.readLine("Enter the username :: ");
        if(username.isEmpty() || username.length() < 6) { addUser("Invalid user username, must be more than 6 characters"); return; }
        if(usersManager.findUserByUsername(username) != null) { addUser("User already exists"); return; }
        
        String password = lineReader.readLine("Enter the password :: ", '*');
        if(password.isEmpty() || password.length() < 6) { addUser("Invalid user password, must be more than 6 characters"); return; }
        
        String userType = lineReader.readLine("Choose a user type (Admin/Inventory/Marketing/Sales) :: ");
        if(!userType.equals("Admin") && !userType.equals("Inventory") && !userType.equals("Marketing") && !userType.equals("Sales")) { addUser("Invalid user type"); return; }
        
        switch(userType) {
            case "Admin":
                usersManager.addUser(new Admin(name, username, password));
                break;
            case "Inventory":
                usersManager.addUser(new Inventory(name, username, password));
                break;
            case "Marketing":
                usersManager.addUser(new Marketing(name, username, password));
                break;
            case "Sales":
                usersManager.addUser(new Sales(name, username, password));
                break;
        }

        terminal.writer().println("User added successfully");
        waitForEnterKey();
    }

    private void deleteUser(String errorMesage) {
        clear();
        printTitle();

        if(errorMesage != null) displayErrorMessage(errorMesage);

        String usernameorid = lineReader.readLine("Enter the username or id :: ");
        User u;
        if(isInteger(usernameorid)) u = usersManager.findUserById(Integer.parseInt(usernameorid));
        else u = usersManager.findUserByUsername(usernameorid);
        if(u == null) { deleteUser("User not found"); return; }

        usersManager.removeUser(u);
        terminal.writer().println("User deleted successfully");
        waitForEnterKey();
    }

    private void addProduct(String errorMessage) {
        clear();
        printTitle();

        if(errorMessage != null) displayErrorMessage(errorMessage);

        String name = lineReader.readLine("Enter the product name :: ");
        if(name.isEmpty() || name.length() < 6) { addProduct("Invalid product name, must be more than 6 characters"); return; }

        String categoryStr = lineReader.readLine("Enter the product category (FOOD/DRINKS/ELECTRONICS/CLEANING/OTHER) :: ");
        Category category;
        try {
            category = Category.valueOf(categoryStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            addProduct("Invalid product category. Must be FOOD, DRINKS, ELECTRONICS, CLEANING, or OTHER");
            return;
        }

        String unitPrice = lineReader.readLine("Enter the product unit price :: ");
        if(!isDouble(unitPrice) && !isInteger(unitPrice)) { addProduct("Invalid product unit price"); return; }

        String stockQuantity = lineReader.readLine("Enter the product stock quantity :: ");
        if(!isInteger(stockQuantity)) { addProduct("Invalid product stock quantity"); return; }

        String lowStockQuantityThreshold = lineReader.readLine("Enter the product low stock quantity threshold :: ");
        if(!isInteger(lowStockQuantityThreshold)) { addProduct("Invalid product low stock quantity threshold"); return; }

        String productType = lineReader.readLine("Enter the product type (Perishable/Non-Perishable) :: ");
        
        if(productType.equalsIgnoreCase("Perishable")) {
            String expiryDate = lineReader.readLine("Enter the product expiry date (YYYY-MM-DD) :: ");
            try {
                LocalDate parsedDate = LocalDate.parse(expiryDate);
                inventoryManager.addProduct(new PerishableProduct(name, category, Double.parseDouble(unitPrice), Integer.parseInt(stockQuantity), Integer.parseInt(lowStockQuantityThreshold), parsedDate));
            } catch (DateTimeParseException e) {
                addProduct("Invalid expiry date format. Use YYYY-MM-DD (e.g., 2025-12-31)");
                return;
            }
        }
        else if(productType.equalsIgnoreCase("Non-Perishable")) {
            String warrantyMonths = lineReader.readLine("Enter the product warranty months :: ");
            if(!isInteger(warrantyMonths)) { addProduct("Invalid product warranty months"); return; }
            inventoryManager.addProduct(new NonPerishableProduct(name, category, Double.parseDouble(unitPrice), Integer.parseInt(stockQuantity), Integer.parseInt(lowStockQuantityThreshold), Integer.parseInt(warrantyMonths)));
        } else { addProduct("Invalid product type. Must be Perishable or Non-Perishable"); return; }

        terminal.writer().println("Product added successfully");
        waitForEnterKey();
    }

    private void deleteProduct(String errorMessage) {
        clear();
        printTitle();

        if(errorMessage != null) displayErrorMessage(errorMessage);

        String productId = lineReader.readLine("Enter the product ID :: ");
        if(productId.isEmpty()) { deleteProduct("Product ID cannot be empty"); return; }
        if(!isInteger(productId)) { deleteProduct("Invalid product ID"); return; }
        Product p = inventoryManager.findProductById(Integer.parseInt(productId));
        if(p == null) { deleteProduct("Product not found"); return; }

        inventoryManager.removeProduct(p);
        terminal.writer().println("Product deleted successfully");
        waitForEnterKey();
    }

    private void setProductDiscount(String errorMessage) {
        clear();
        printTitle();

        if(errorMessage != null) displayErrorMessage(errorMessage);

        String productId = lineReader.readLine("Enter the product ID :: ");
        if(!isInteger(productId)) { setProductDiscount("Invalid product ID"); return; }
        
        Product p = inventoryManager.findProductById(Integer.parseInt(productId));
        if(p == null) { setProductDiscount("Product not found"); return; }

        terminal.writer().println("\nProduct: " + p.getName() + " (ID: " + p.getProductId() + ")");
        terminal.writer().println("Current discount: " + p.getDiscountStrategy().getDiscountStrategy());
        terminal.writer().flush();

        String discountType = lineReader.readLine("\nEnter discount type (None/BuyXGetYFree) :: ");
        
        if(discountType.equalsIgnoreCase("None")) {
            p.setDiscountStrategy(new offers.NoDiscount());
            inventoryManager.saveProducts();
            terminal.writer().println("Discount removed successfully");
        } else if(discountType.equalsIgnoreCase("BuyXGetYFree")) {
            String buyQty = lineReader.readLine("Enter buy quantity (X) :: ");
            if(!isInteger(buyQty) || Integer.parseInt(buyQty) <= 0) { setProductDiscount("Invalid buy quantity"); return; }
            
            String freeQty = lineReader.readLine("Enter free quantity (Y) :: ");
            if(!isInteger(freeQty) || Integer.parseInt(freeQty) <= 0) { setProductDiscount("Invalid free quantity"); return; }
            
            p.setDiscountStrategy(new offers.BuyXGetYFree(Integer.parseInt(buyQty), Integer.parseInt(freeQty)));
            inventoryManager.saveProducts();
            terminal.writer().println("Discount set successfully: Buy " + buyQty + " Get " + freeQty + " Free");
        } else {
            setProductDiscount("Invalid discount type. Must be None or BuyXGetYFree");
            return;
        }

        waitForEnterKey();
    }

    private void newSale(String errorMessage) {
        clear();
        printTitle();

        if(errorMessage != null) displayErrorMessage(errorMessage);
        List<SaleItem> items = new ArrayList<>();

        while(true) {
            String productId = lineReader.readLine("Enter the product ID ( or 'done' to finalize ) :: ");
            if(productId.equalsIgnoreCase("done")) {
                if(items.isEmpty()) {
                    terminal.writer().println("No items added. Sale cancelled.");
                    waitForEnterKey();
                    return;
                } else {
                    String discountType = lineReader.readLine("Enter the discount type ( None/Percentage ) :: ");
                    if(!discountType.equalsIgnoreCase("None") && !discountType.equalsIgnoreCase("Percentage")) { newSale("Invalid discount type"); return; }

                    DiscountStrategy strat;

                    if(discountType.equalsIgnoreCase("None")) strat = new NoDiscount();
                    else {
                        String discountPercentage = lineReader.readLine("Enter discount percentage :: ");
                        if(!isDouble(discountPercentage) || Double.parseDouble(discountPercentage) <= 0 || Double.parseDouble(discountPercentage) > 100) { newSale("Invalid discount percentage"); return; }

                        strat = new PercentageDiscount(Double.parseDouble(discountPercentage));
                    }

                    Sale sale = new Sale(items, strat);
                    List<Sale> sales = FileManager.loadSales();

                    sales.add(sale);
                    FileManager.saveSales(sales);
                    sale.generateReceipt();

                    waitForEnterKey();
                    break;
                }
            } else {
                Product p = inventoryManager.findProductById(Integer.parseInt(productId));
                if(p == null) { newSale("Product not found"); return; }

                terminal.writer().println(" - " + p.getName() + " (ID: " + p.getProductId() + ")");
                terminal.writer().println(" - Price: $" + p.getUnitPrice());
                String quantity = lineReader.readLine("Enter the quantity :: ");
                if(!isInteger(quantity) || Integer.parseInt(quantity) <= 0) { newSale("Invalid quantity"); return; }

                SaleItem item = new SaleItem(p, Integer.parseInt(quantity));
                items.add(item);
            }
        }
    }

    private void viewSalesSummary() {
        clear();
        printTitle();

        List<Sale> sales = FileManager.loadSales();
        
        if(sales.isEmpty()) {
            terminal.writer().println("No sales data available.");
            waitForEnterKey();
            return;
        }

        int totalSalesCount = sales.size();
        double totalRevenue = 0;
        Map<Category, Double> revenuePerCategory = new HashMap<>();

        for(Sale s : sales) {
            totalRevenue += s.getTotal();
            for(SaleItem item : s.getSaleItems()) {
                if(item.getProduct() != null) {
                    Category cat = item.getProduct().getCategory();
                    revenuePerCategory.put(cat, revenuePerCategory.getOrDefault(cat, 0.0) + item.getSaleTotalPrice());
                }
            }
        }

        SalesReporter reporter = new SalesReporter(totalSalesCount, totalRevenue, revenuePerCategory);

        terminal.writer().printf("Total Sales: %d\n", reporter.getTotalSalesCount());
        terminal.writer().printf("Total Revenue: $%.2f\n", reporter.getTotalRevenue());
        terminal.writer().printf("Average Sale Value: $%.2f\n", reporter.getAverageSalesValue());
        
        terminal.writer().println("\n ─ Revenue by Category ");
        for(Map.Entry<Category, Double> entry : reporter.getRevenuePerCategory().entrySet()) {
            terminal.writer().printf("%s: $%.2f\n", entry.getKey(), entry.getValue());
        }

        Category bestCategory = reporter.findBestSellingCategory();
        if(bestCategory != null) terminal.writer().printf("\nBest Selling Category: %s\n", bestCategory);

        terminal.writer().flush();
        waitForEnterKey();
    }
}
