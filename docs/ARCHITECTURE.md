# System Architecture

This document describes the technical architecture of the Hypermarket Management System.

## System Overview

The Hypermarket Management System follows a layered architecture with clear separation of concerns:

```mermaid
graph TB
    subgraph Presentation["Presentation Layer"]
        UI[ConsoleUI]
    end

    subgraph Business["Business Logic Layer"]
        IM[InventoryManager]
        UM[UsersManager]
    end

    subgraph Domain["Domain Layer"]
        P[Product]
        S[Sale]
        U[User]
        D[DiscountStrategy]
    end

    subgraph Persistence["Persistence Layer"]
        FM[FileManager]
        CSV[(CSV Files)]
    end

    UI --> IM
    UI --> UM
    IM --> P
    IM --> S
    UM --> U
    S --> D
    IM --> FM
    UM --> FM
    FM --> CSV
```

## Package Structure

| Package    | Responsibility                     | Key Classes                                                  |
| ---------- | ---------------------------------- | ------------------------------------------------------------ |
| `ui`       | Console user interface             | `ConsoleUI`                                                  |
| `managers` | Business logic and data management | `InventoryManager`, `UsersManager`, `FileManager`            |
| `product`  | Product domain model               | `Product`, `PerishableProduct`, `NonPerishableProduct`       |
| `sales`    | Sales domain model                 | `Sale`, `SaleItem`, `SalesReporter`                          |
| `users`    | User domain model                  | `User`, `Admin`, `Inventory`, `Marketing`, `Sales`           |
| `offers`   | Discount strategies                | `DiscountStrategy`, `NoDiscount`, `PercentageDiscount`, etc. |
| `enums`    | Type enumerations                  | `Category`, `UserType`, `ProductType`, `DiscountStrategies`  |

## Class Hierarchies

### Product Hierarchy

```mermaid
classDiagram
    class Product {
        <<abstract>>
        -int productId
        -String name
        -Category category
        -double unitPrice
        -int stockQuantity
        -int lowStockQuantityThreshold
        +increaseStock(int)
        +decreaseStock(int)
        +isLowStock() bool
        +getProductType()* String
    }

    class PerishableProduct {
        -LocalDate expiryDate
        -int DAYS_UNTIL_NEAR_EXPIRY = 4
        +isExpired() bool
        +isNearExpiry() bool
        +getExpiryDate() LocalDate
    }

    class NonPerishableProduct {
        -int warrantyMonths
        +getWarrantyMonths() int
    }

    Product <|-- PerishableProduct
    Product <|-- NonPerishableProduct
```

### User Hierarchy

```mermaid
classDiagram
    class User {
        <<abstract>>
        #int userId
        #String name
        #String username
        #String password
        +verifyPassword(String) bool
        +getUserType()* UserType
    }

    class Admin {
        +getUserType() UserType
    }

    class Inventory {
        +getUserType() UserType
    }

    class Marketing {
        +getUserType() UserType
    }

    class Sales {
        +getUserType() UserType
    }

    User <|-- Admin
    User <|-- Inventory
    User <|-- Marketing
    User <|-- Sales
```

### Discount Strategy Pattern

```mermaid
classDiagram
    class DiscountStrategy {
        <<interface>>
        +applyDiscount(double subtotal) double
        +getDiscountStrategy() DiscountStrategies
    }

    class NoDiscount {
        +applyDiscount(double) double
    }

    class PercentageDiscount {
        -double percentage
        +applyDiscount(double) double
    }

    class FixedDiscount {
        -double discountAmount
        +applyDiscount(double) double
    }

    class BuyXGetYFree {
        -int buyQuantity
        -int freeQuantity
        +applyDiscount(double) double
    }

    DiscountStrategy <|.. NoDiscount
    DiscountStrategy <|.. PercentageDiscount
    DiscountStrategy <|.. FixedDiscount
    DiscountStrategy <|.. BuyXGetYFree
```

## Composition Relationships

### Sale Composition

```mermaid
classDiagram
    class Sale {
        -int saleId
        -LocalDate saleDate
        -List~SaleItem~ items
        -DiscountStrategy discountStrategy
        -double subTotal
        -double discountAmount
        -double totalAmount
        +addSaleItem(SaleItem)
        +removeSaleItem(SaleItem)
        +processSale()
        +generateReceipt()
    }

    class SaleItem {
        -Product product
        -int quantity
        -double saleTotalPrice
        +getProduct() Product
        +getQuantity() int
        +getSaleTotalPrice() double
    }

    class Product {
        <<abstract>>
    }

    Sale "1" *-- "*" SaleItem : contains
    SaleItem "1" --> "1" Product : references
    Sale --> DiscountStrategy : uses
```

## Data Flow

### Sale Processing Flow

```mermaid
sequenceDiagram
    participant UI as ConsoleUI
    participant IM as InventoryManager
    participant S as Sale
    participant SI as SaleItem
    participant P as Product
    participant FM as FileManager

    UI->>S: Create new Sale
    loop Add Items
        UI->>IM: findProductById(id)
        IM-->>UI: Product
        UI->>SI: Create SaleItem(product, qty)
        UI->>S: addSaleItem(saleItem)
        S->>S: recalcTotals()
    end
    UI->>S: processSale()
    loop Update Stock
        S->>P: decreaseStock(qty)
    end
    S->>FM: saveSales(allSales)
    S->>FM: saveProducts(products)
    S->>S: generateReceipt()
```

## File Persistence Design

### FileManager Responsibilities

```mermaid
flowchart LR
    subgraph FileManager
        LP[loadProducts]
        SP[saveProducts]
        LS[loadSales]
        SS[saveSales]
        LU[loadUsers]
    end

    subgraph CSV["CSV Files"]
        PC[products.csv]
        SC[sales.csv]
        UC[users.csv]
    end

    LP --> PC
    SP --> PC
    LS --> SC
    SS --> SC
    LU --> UC
```

### File Format Design

**Products CSV:**

```
id, name, category, unitPrice, stockQty, lowStockThreshold, type, typeSpecificField
```

- `typeSpecificField` = expiryDate (PERISHABLE) or warrantyMonths (NON_PERISHABLE)

**Sales CSV:**

```
id, date, subtotal, discount, total, items
```

- `items` format: `productId:quantity;productId:quantity;...`

**Users CSV:**

```
userId, role, name, username, password
```

## UI Architecture

### Console UI with JLine

The application uses **JLine 3** library for enhanced console interaction:

- **Arrow key navigation** - Up/Down to select menu items
- **Password masking** - Hidden input for password entry
- **Screen clearing** - Clean transitions between views
- **Colored output** - AttributedString for styled text

### Menu Flow

```mermaid
stateDiagram-v2
    [*] --> Login
    Login --> AdminMenu: Admin user
    Login --> InventoryMenu: Inventory user
    Login --> MarketingMenu: Marketing user
    Login --> SalesMenu: Sales user

    AdminMenu --> ViewUsers
    AdminMenu --> ViewProducts
    AdminMenu --> ViewDiscounts
    AdminMenu --> Login: Logout
    AdminMenu --> [*]: Exit

    InventoryMenu --> ViewProducts
    InventoryMenu --> SearchProducts
    InventoryMenu --> ViewReports
    InventoryMenu --> Login: Logout
    InventoryMenu --> [*]: Exit

    MarketingMenu --> ViewDiscounts
    MarketingMenu --> Login: Logout
    MarketingMenu --> [*]: Exit

    SalesMenu --> ViewSales
    SalesMenu --> NewSale
    SalesMenu --> TopProducts
    SalesMenu --> Login: Logout
    SalesMenu --> [*]: Exit
```

## Key Design Decisions

### 1. Abstract Base Classes

Both `Product` and `User` are abstract classes requiring subclasses to implement type-specific behavior (`getProductType()`, `getUserType()`).

### 2. Strategy Pattern for Discounts

Discounts are implemented using the Strategy pattern, allowing easy addition of new discount types without modifying existing code.

### 3. Composition over Inheritance

`Sale` contains `SaleItem` objects (composition) rather than inheriting from a collection, providing better encapsulation and control.

### 4. File-Based Persistence

CSV files are used for simplicity and human readability, with `FileManager` centralizing all I/O operations.

### 5. Role-Based Access Control

Different user types have different menu options, implemented through polymorphism and separate menu display methods.

## Validation & Error Handling

- **Constructors** validate all input parameters
- **Setters** include validation logic with descriptive exceptions
- **Stock operations** prevent negative inventory
- **User input** is validated before processing
- **File I/O** uses try-catch with error logging
