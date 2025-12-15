package ui;

import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.MaskingCallback;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;

import managers.*;
import users.User;

public class ConsoleUI {
    private final UsersManager usersManager = new UsersManager();
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
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
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
            "View Near Expiry Products",
            "View Expired Products",
            "View Low Stock Products",
            "────",
            "View Foods",
            "View Drinks",
            "View Electronics",
            "View Cleaning Products",
            "View Other Products",
            "Search Products",
            "────",
            "Logout",
            "Exit"
        };

        Runnable[] actions = {
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
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
            null,
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
            "New Sale",
            "────",
            "View Top Selling Products",
            "View Least Selling Products",
            "────",
            "Logout",
            "Exit" 
        };

        Runnable[] actions = {
            null,
            null,
            null,
            null,
            null,
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
                
                terminal.writer().println("\nUse ↑↓ arrows to navigate and ⏎ Enter to select\r");
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
                            return;
                        }
                        inSubmenu = false;
                        subMenuSelectedIdx = 0;
                    } else {
                        if (subMenuTitles != null && subMenuTitles[selectedIdx] != null) {
                            inSubmenu = true;
                            subMenuSelectedIdx = 0;
                        } else if (actions[selectedIdx] != null) {
                            actions[selectedIdx].run();
                            return;
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
}
