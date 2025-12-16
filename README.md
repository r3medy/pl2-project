# Hypermarket Management System

A comprehensive Java console application for managing hypermarket back-office operations. Built with Object-Oriented Programming principles, featuring role-based access control, inventory management, sales processing, and detailed reporting.

## ✨ Features

### Core Features

- **Product Management** - Add, view, search products by ID or name
- **Category Management** - Filter products by category (Food, Drinks, Electronics, Cleaning, Other)
- **Stock Management** - Track inventory levels with low-stock alerts
- **Sales Processing** - Create sales with multiple items, generate receipts
- **File Persistence** - All data persists across sessions via CSV files

### Advanced Features

- **Product Inheritance** - Perishable products (with expiry dates) and Non-Perishable products (with warranty)
- **Discount Strategies** - Strategy pattern implementation (NoDiscount, PercentageDiscount, FixedDiscount, BuyXGetYFree)
- **Expiry Tracking** - Near-expiry and expired product reports
- **Sales Analytics** - Top/least selling products with units sold count
- **Role-Based Access** - Different menus for Admin, Inventory, Marketing, and Sales users

## 📋 Prerequisites

- **Java 8** or higher
- **JLine 3** library (included in `lib/` folder)

## 🚀 Quick Start

### Clone the Repository

```bash
git clone <repository-url>
cd JavaProject
```

### Compile

```bash
# Windows
compile.bat

# Or manually
javac -cp "lib/*" -d out src/**/*.java
```

### Run

```bash
# Windows
run.bat

# Or manually
java -cp "out;lib/*" Main
```

## 👤 Default Users

| Role      | Username      | Password   |
| --------- | ------------- | ---------- |
| Admin     | `yousef`      | `admin123` |
| Inventory | `ahmed.inv`   | `inv123`   |
| Marketing | `nour.mkt`    | `mkt123`   |
| Sales     | `karim.sales` | `sales123` |

## 📖 User Guide

### Admin Menu

- View/Add/Remove Users
- View/Add/Remove Products
- View/Create/Remove Discounts
- Logout/Exit

### Inventory Menu

- View All Products
- Search Products (by ID or Name)
- View Near Expiry/Expired Products
- View Low Stock Products
- Filter by Category (Food, Drinks, Electronics, Cleaning, Other)
- Logout/Exit

### Marketing Menu

- View/Create/Remove Discounts
- Logout/Exit

### Sales Menu

- View Sales
- View Sale Details by ID
- New Sale
- View Top/Least Selling Products
- Logout/Exit

## 📁 Project Structure

```
JavaProject/
├── src/
│   ├── Main.java              # Application entry point
│   ├── enums/                 # Enumerations
│   │   ├── Actions.java
│   │   ├── Category.java      # FOOD, DRINKS, ELECTRONICS, CLEANING, OTHER
│   │   ├── DiscountStrategies.java
│   │   ├── ProductType.java   # PERISHABLE, NON_PERISHABLE
│   │   └── UserType.java      # ADMIN, INVENTORY, MARKETING, SALES
│   ├── managers/              # Business logic managers
│   │   ├── FileManager.java   # CSV file I/O operations
│   │   ├── InventoryManager.java
│   │   └── UsersManager.java
│   ├── offers/                # Discount strategy pattern
│   │   ├── DiscountStrategy.java  # Interface
│   │   ├── NoDiscount.java
│   │   ├── PercentageDiscount.java
│   │   ├── FixedDiscount.java
│   │   └── BuyXGetYFree.java
│   ├── product/               # Product hierarchy
│   │   ├── Product.java       # Abstract base class
│   │   ├── PerishableProduct.java
│   │   └── NonPerishableProduct.java
│   ├── sales/                 # Sales domain
│   │   ├── Sale.java
│   │   ├── SaleItem.java
│   │   └── SalesReporter.java
│   ├── ui/                    # User interface
│   │   └── ConsoleUI.java     # JLine-based console UI
│   └── users/                 # User hierarchy
│       ├── User.java          # Abstract base class
│       ├── Admin.java
│       ├── Inventory.java
│       ├── Marketing.java
│       └── Sales.java
├── data/                      # Persistent data files
│   ├── products.csv
│   ├── sales.csv
│   ├── users.csv
│   └── discounts.csv
├── docs/                      # Documentation
│   ├── REQUIREMENTS.md
│   ├── ARCHITECTURE.md
│   └── Class Diagram.svg
├── lib/                       # External libraries
│   └── jline-3.x.jar
├── compile.bat                # Compilation script
└── run.bat                    # Run script
```

## 📊 Data File Formats

### products.csv

```csv
id, name, category, unitPrice, stockQuantity, lowStockThreshold, type, expiryDate/warrantyMonths
1, Milk 1L, FOOD, 1.25, 120, 20, PERISHABLE, 2025-01-20
16, LED Bulb, ELECTRONICS, 3.50, 200, 30, NON_PERISHABLE, 24
```

### sales.csv

```csv
id, date, subtotal, discount, total, items
1, 2024-01-05, 15.45, 0, 15.45, 1:3;3:5;12:4
```

Items format: `productId:quantity;productId:quantity;...`

### users.csv

```csv
userId, role, name, username, password
1, ADMIN, Yousef Adel, yousef, admin123
```

## 🏗️ Design Patterns

### Strategy Pattern (Discounts)

```
DiscountStrategy (interface)
├── NoDiscount
├── PercentageDiscount
├── FixedDiscount
└── BuyXGetYFree
```

### Inheritance (Products & Users)

```
Product (abstract)          User (abstract)
├── PerishableProduct       ├── Admin
└── NonPerishableProduct    ├── Inventory
                            ├── Marketing
                            └── Sales
```

### Composition

- `Sale` contains multiple `SaleItem` objects
- `SaleItem` references a `Product`
- `InventoryManager` manages lists of `Product` and `Sale`

## 🎓 OOP Concepts Demonstrated

- **Encapsulation** - Private fields with getters/setters, validation in setters
- **Inheritance** - Product and User hierarchies
- **Polymorphism** - Abstract methods (`getProductType()`, `getUserType()`)
- **Composition** - Sale-SaleItem relationship
- **Abstraction** - Abstract classes and interfaces
- **Strategy Pattern** - Discount strategies
