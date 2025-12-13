package ui;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.jline.utils.InfoCmp;

public class ConsoleUI {
    // In Class-diagram
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
                "Back"
            },
            {
                "New Sale",
                "View Sales",
                "Back"
            },
            {
                "Daily Report",
                "Monthly Report",
                "Back"
            },
            null
        };

        Runnable[][] subMenuActions = {
            {
                () -> System.out.println("Adding product..."),
                () -> System.out.println("Viewing products..."),
                null
            },
            {
                () -> System.out.println("New sale..."),
                () -> System.out.println("Viewing sales..."),
                null
            },
            {
                () -> System.out.println("Daily report..."),
                () -> System.out.println("Monthly report..."),
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
        System.exit(0);
    }
    

    private void displayMenu(String[] titles, Runnable[] actions, String[][] subMenuTitles, Runnable[][] subMenuActions) {
        try {
            Terminal terminal = TerminalBuilder.terminal();
            terminal.enterRawMode();

            int selectedIdx = 0;
            int subMenuSelectedIdx = 0;
            boolean inSubmenu = false;

            while (true) {
                terminal.puts(InfoCmp.Capability.clear_screen);
                terminal.writer().flush();
                for (int i = 0; i < titles.length; i++) {
                    terminal.writer().print(getOptionStyle(titles[i], i == selectedIdx, false).toAnsi() + "\r\n");

                    if (i == selectedIdx && inSubmenu && subMenuTitles[i] != null)
                        for (int j = 0; j < subMenuTitles[i].length; j++)
                            terminal.writer().print(getOptionStyle(subMenuTitles[i][j], j == subMenuSelectedIdx, true).toAnsi() + "\r\n");
                }
                
                terminal.writer().flush();
                int initialKey = terminal.reader().read();

                // Arrow keys:
                // ESC (27) + '[' (91) OR 'O' (79) + A/B/C/D
                if (initialKey == 27) {
                    int secondKey = terminal.reader().read();
                    if (secondKey == 91 || secondKey == 79) {
                        int arrowDir = terminal.reader().read();
                        switch (arrowDir) {
                            case 65: // Up
                                if (inSubmenu) subMenuSelectedIdx = Math.max(0, subMenuSelectedIdx - 1);
                                else selectedIdx = Math.max(0, selectedIdx - 1);
                                break;
                            case 66: // Down
                                if (inSubmenu) subMenuSelectedIdx = Math.min(subMenuTitles[selectedIdx].length - 1, subMenuSelectedIdx + 1);
                                else selectedIdx = Math.min(titles.length - 1, selectedIdx + 1);
                                break;
                        }
                    }
                } else if (initialKey == 13 || initialKey == 10) {
                    if (inSubmenu) {
                        if (subMenuActions[selectedIdx] != null && subMenuActions[selectedIdx][subMenuSelectedIdx] != null) {
                            terminal.puts(InfoCmp.Capability.clear_screen);
                            terminal.writer().flush();
                            subMenuActions[selectedIdx][subMenuSelectedIdx].run();
                            break;
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
            new AttributedString((isSubmenu ? "  │   " : "") + optionText);
    }
}
