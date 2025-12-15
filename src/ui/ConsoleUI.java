package ui;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import enums.Category;
import managers.FileManager;
import managers.InventoryManager;
import offers.*;
import product.*;
import sales.*;

public class ConsoleUI {
    private final InventoryManager inventoryManager = new InventoryManager();
    private final Scanner scanner = new Scanner(System.in);
    private Terminal terminal;

    public ConsoleUI() {}

    public void start() {
        String[] titles = {
            "Products",
            "Sales",
            "Reports",
            "Exit"
        };
        Runnable[] actions = {
            null,
            null,
            null,
            this::exit
        };

        String[][] subMenuTitles = {
            {
                "Add Product",
                "View Products",
                "Search Product",
                "Back"
            },
            {
                "New Sale",
                "View Sales",
                "Back"
            },
            {
                "Low Stock Report",
                "Sales Summary",
                "Top Selling Products",
                "Expired Products",
                "Near Expiry Products",
                "Back"
            },
            null
        };

        Runnable[][] subMenuActions = {
            {
                null,
                null,
                null,
                null
            },
            {
                null,
                null,
                null
            },
            {
                null,
                null,
                null,
                null,
                null,
                null
            },
            null
        };

        this.displayMenu(titles, actions, subMenuTitles, subMenuActions);
    }

    public void displayProductsMenu() {}
    public void displaySalesMenu() {}
    public void displayReportsMenu() {}
    
    public void exit() {
        System.out.println("\nGoodbye!");
        for (Product p : inventoryManager.getProducts()) {
            p.productInformation();
            System.out.println("\n------------------------\n");
        }
        System.exit(0);
    }

    private void displayMenu(String[] titles, Runnable[] actions, String[][] subMenuTitles, Runnable[][] subMenuActions) {
        try {
            terminal = TerminalBuilder.terminal();
            terminal.enterRawMode();

            int selectedIdx = 0;
            int subMenuSelectedIdx = 0;
            boolean inSubmenu = false;

            while (true) {
                terminal.puts(InfoCmp.Capability.clear_screen);
                terminal.writer().flush();
                
                terminal.writer().println("========== HYPERMARKET MANAGEMENT SYSTEM ==========\r");
                
                for (int i = 0; i < titles.length; i++) {
                    terminal.writer().print(getOptionStyle(titles[i], i == selectedIdx, false).toAnsi() + "\r\n");

                    if (i == selectedIdx && inSubmenu && subMenuTitles[i] != null)
                        for (int j = 0; j < subMenuTitles[i].length; j++)
                            terminal.writer().print(getOptionStyle(subMenuTitles[i][j], j == subMenuSelectedIdx, true).toAnsi() + "\r\n");
                }
                
                terminal.writer().println("Use ↑↓ arrows to navigate, Enter to select\r");
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
                            terminal.puts(InfoCmp.Capability.clear_screen);
                            terminal.writer().flush();
                            terminal.close();
                            
                            subMenuActions[selectedIdx][subMenuSelectedIdx].run();
                            start();
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
            new AttributedString((isSubmenu ? "  ├─ " : "→ ") + optionText, AttributedStyle.DEFAULT.foreground(isSubmenu ? AttributedStyle.YELLOW : AttributedStyle.CYAN)) :
            new AttributedString((isSubmenu ? "  │   " : "  ") + optionText);
    }
}
