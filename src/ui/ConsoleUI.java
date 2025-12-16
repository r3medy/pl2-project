package ui;

import java.util.List;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;

import enums.*;
import managers.*;
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

    // public void start() {
    //     Runnable[] actions = {
    //         null,
    //         null,
    //         null,
    //         this::exit
    //     };

    //     String[][] subMenuTitles = {
    //         {
    //             "Add Product",
    //             "View Products",
    //             "Search Product",
    //             "Back"
    //         },
    //         {
    //             "New Sale",
    //             "View Sales",
    //             "Back"
    //         },
    //         {
    //             "Low Stock Report",
    //             "Sales Summary",
    //             "Top Selling Products",
    //             "Expired Products",
    //             "Near Expiry Products",
    //             "Back"
    //         },
    //         null
    //     };

    //     Runnable[][] subMenuActions = {
    //         {
    //             null,
    //             null,
    //             null,
    //             null
    //         },
    //         {
    //             null,
    //             null,
    //             null
    //         },
    //         {
    //             null,
    //             null,
    //             null,
    //             null,
    //             null,
    //             null
    //         },
    //         null
    //     };

    //     this.displayMenu(titles, actions, subMenuTitles, subMenuActions);
    // }

    public void displayAdminMenu() {
        String[] titles = {
            "View Users",
            "Add User",
            "Remove User",
            "────",
            "View Products",
            "Add Product",
            "Remove Product",
            "────",
            "View Discounts",
            "Create Discount",
            "Remove Discount",
            "────",
            "Logout",
            "Exit"
        };

        Runnable[] actions = {
            () -> this.viewUsers(),
            null,
            null,
            null,
            () -> this.viewProducts(),
            null,
            null,
            null,
            () -> this.viewDiscounts(),
            null,
            null,
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
            "View Foods",
            "View Drinks",
            "View Electronics",
            "View Cleaning Products",
            "View Other Products",
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
            () -> this.viewProductsByCategory(Category.FOOD),
            () -> this.viewProductsByCategory(Category.DRINKS),
            () -> this.viewProductsByCategory(Category.ELECTRONICS),
            () -> this.viewProductsByCategory(Category.CLEANING),
            () -> this.viewProductsByCategory(Category.OTHER),
            null,
            () -> this.start(null),
            this::exit
        };

        this.displayMenu(titles, actions, null, null);
    }
    
    public void displayMarketingMenu() {
        String[] titles = {
            "View Discounts",
            "Create Discount",
            "Remove Discount",
            "────",
            "Logout",
            "Exit"
        };

        Runnable[] actions = {
            () -> this.viewDiscounts(),
            null,
            null,
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
            "View Top Selling Products",
            "View Least Selling Products",
            "────",
            "Logout",
            "Exit" 
        };

        Runnable[] actions = {
            () -> this.viewSales(),
            () -> this.viewSaleDetailsById(null),
            null,
            null,
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

    private void viewUsers() {
        clear();
        printTitle();
        terminal.writer().printf("%5s %20s %20s %20s%n%n", "ID", "Name", "Username", "Type");
        for(User u : usersManager.getUsers()) {
            terminal.writer().printf("%5d %20s %20s %20s%n", u.getUserId(), u.getName(), u.getUsername(), u.getUserType());
        }
        waitForEnterKey();
    }
    
    private void viewProducts() {
        clear();
        printTitle();
        terminal.writer().printf("%5s %20s %10s %15s %15s %5s %15s%n%n", "ID", "Name", "Price", "Stock Qty.", "Category", "Low Stock?", "Expiry Date/Warranty Months");
        for(Product p : inventoryManager.getProducts()) {
            terminal.writer().printf("%5d %20s %10s %15s %15s %5s %15s%n",
                p.getProductId(),
                p.getName(),
                "$" + p.getUnitPrice(),
                p.getStockQuantity(),
                p.getCategory(),
                p.isLowStock() ? "Yes" : "No",
                (p instanceof PerishableProduct) ? ((PerishableProduct) p).getExpiryDate() : ((NonPerishableProduct) p).getWarrantyMonths() + " Months"
            );
        }

        waitForEnterKey();
    }

    private void viewDiscounts() {
        clear();
        printTitle();
        terminal.writer().printf("%10s %15s %15s %15s %15s%n%n", "SaleID", "Date", "Subtotal", "Discount", "Total");
        
        List<Sale> sales = FileManager.loadSales();
        for (Sale s : sales) {
            if (s.getDiscountAmount() > 0) {
                terminal.writer().printf("%10d %15s %15.2f$ %15.2f$ %15.2f$%n",
                    s.getSaleId(),
                    s.getSaleDate(),
                    s.getSubTotal(),
                    s.getDiscountAmount(),
                    s.getTotalAmount()
                );
            }
        }
        
        waitForEnterKey();
    }

    private void viewNearExpiryProducts() {
        clear();
        printTitle();
        terminal.writer().printf("%5s %20s %10s %15s %15s %5s %15s%n%n", "ID", "Name", "Price", "Stock Qty.", "Category", "Near Expiry?", "Expiry Date");
        for(Product p : inventoryManager.listNearExpiryProducts()) {
            terminal.writer().printf("%5d %20s %10s %15s %15s %5s %15s%n",
                p.getProductId(),
                p.getName(),
                "$" + p.getUnitPrice(),
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
        terminal.writer().printf("%5s %20s %10s %15s %15s %5s %15s%n%n", "ID", "Name", "Price", "Stock Qty.", "Category", "Expired?", "Expiry Date");
        for(Product p : inventoryManager.listExpiredProducts()) {
            if (!(p instanceof PerishableProduct)) continue;
                terminal.writer().printf("%5d %20s %10s %15s %15s %5s %15s%n",
                p.getProductId(),
                p.getName(),
                "$" + p.getUnitPrice(),
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
        terminal.writer().printf("%5s %20s %10s %15s %15s %5s%n%n", "ID", "Name", "Price", "Stock Qty.", "Category", "Low Stock?");
        for(Product p : inventoryManager.listLowStockProducts()) {
            if (!(p instanceof PerishableProduct)) continue;
                terminal.writer().printf("%5d %20s %10s %15s %15s %5s%n",
                p.getProductId(),
                p.getName(),
                "$" + p.getUnitPrice(),
                p.getStockQuantity(),
                p.getCategory(),
                ((PerishableProduct)p).isLowStock() ? "Yes" : "No"
            );
        }
        waitForEnterKey();
    }
    
    private void viewProductsByCategory(Category category) {
        clear();
        printTitle();
        terminal.writer().printf("%5s %20s %10s %15s %15s %5s %15s%n%n", "ID", "Name", "Price", "Stock Qty.", "Category", "Low Stock?", "Expiry Date/Warranty Months");
        for(Product p : inventoryManager.listProductsByCategory(category)) {
            terminal.writer().printf("%5d %20s %10s %15s %15s %5s %15s%n",
                p.getProductId(),
                p.getName(),
                "$" + p.getUnitPrice(),
                p.getStockQuantity(),
                p.getCategory(),
                p.isLowStock() ? "Yes" : "No",
                (p instanceof PerishableProduct) ? ((PerishableProduct) p).getExpiryDate() : ((NonPerishableProduct) p).getWarrantyMonths() + " Months"
            );
        }
        waitForEnterKey();
    }

    private void viewSales() {
        clear();
        printTitle();
        terminal.writer().printf("%10s %15s %15s %15s %15s%n%n", "SaleID", "Date", "Subtotal", "Discount", "Total");
        for (Sale s : FileManager.loadSales()) {
            terminal.writer().printf("%10d %15s %15.2f$ %15.2f$ %15.2f$%n",
                s.getSaleId(),
                s.getSaleDate(),
                s.getSubTotal(),
                s.getDiscountAmount(),
                s.getTotalAmount()
            );
        }
        waitForEnterKey();
    }

    private void viewLeastOrTopSellingProducts(int limit, boolean isTop) {
        clear();
        printTitle();
        int ctr=0;
        terminal.writer().printf("%5s %5s %20s %12s %10s %15s %15s %5s %15s%n%n", "No.", "ID", "Name", "Units Sold", "Price", "Stock Qty.", "Category", "Low Stock?", "Expiry/Warranty");
        for(Product p : (isTop ? inventoryManager.listTopSellingProducts(limit) : inventoryManager.listLeastSellingProducts(limit))) {
            ctr++;
            terminal.writer().printf("%5s %5d %20s %12d %10s %15s %15s %5s %15s%n",
                ctr == 1 ? "1st" : ctr == 2 ? "2nd" : ctr == 3 ? "3rd" : ctr + "th",
                p.getProductId(),
                p.getName(),
                inventoryManager.getProductSalesCount(p.getProductId()),
                "$" + p.getUnitPrice(),
                p.getStockQuantity(),
                p.getCategory(),
                p.isLowStock() ? "Yes" : "No",
                (p instanceof PerishableProduct) ? ((PerishableProduct) p).getExpiryDate() : ((NonPerishableProduct) p).getWarrantyMonths() + " Months"
            );
        }
        waitForEnterKey();
    }

    private void searchProductsById(String errorMessage) {
        clear();
        printTitle();
        
        if(errorMessage != null) {
            terminal.writer().println((new AttributedString(errorMessage, AttributedStyle.BOLD.foreground(AttributedStyle.RED))).toAnsi() + "\r");
            terminal.writer().flush();
        }

        String productId = lineReader.readLine("Enter the product ID :: ");
        Product product = inventoryManager.findProductById(Integer.parseInt(productId));
        if(product == null) {
            searchProductsById("Invalid product ID");
            return;
        }

        terminal.writer().printf("%5s %20s %10s %15s %15s %5s %15s%n%n", "ID", "Name", "Price", "Stock Qty.", "Category", "Low Stock?", "Expiry Date/Warranty Months");
        terminal.writer().printf("%5d %20s %10s %15s %15s %5s %15s%n",
            product.getProductId(),
            product.getName(),
            "$" + product.getUnitPrice(),
            product.getStockQuantity(),
            product.getCategory(),
            product.isLowStock() ? "Yes" : "No",
            (product instanceof PerishableProduct) ? ((PerishableProduct) product).getExpiryDate() : ((NonPerishableProduct) product).getWarrantyMonths() + " Months"
        );

        waitForEnterKey();
    }

    private void searchProductsByName(String errorMessage) {
        clear();
        printTitle();
        
        if(errorMessage != null) {
            terminal.writer().println((new AttributedString(errorMessage, AttributedStyle.BOLD.foreground(AttributedStyle.RED))).toAnsi() + "\r");
            terminal.writer().flush();
        }

        String productName = lineReader.readLine("Enter the product name :: ");
        Product product = inventoryManager.findProductByName(productName);
        if(product == null) {
            searchProductsByName("Invalid product name");
            return;
        }

        terminal.writer().printf("%5s %20s %10s %15s %15s %5s %15s%n%n", "ID", "Name", "Price", "Stock Qty.", "Category", "Low Stock?", "Expiry Date/Warranty Months");
        terminal.writer().printf("%5d %20s %10s %15s %15s %5s %15s%n",
            product.getProductId(),
            product.getName(),
            "$" + product.getUnitPrice(),
            product.getStockQuantity(),
            product.getCategory(),
            product.isLowStock() ? "Yes" : "No",
            (product instanceof PerishableProduct) ? ((PerishableProduct) product).getExpiryDate() : ((NonPerishableProduct) product).getWarrantyMonths() + " Months"
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

    private void addUser() {

    }

    private void deleteUser() {}
}
