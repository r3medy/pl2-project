

## Project 04: Hypermarket Management System – Student Project
## Handout
## 1. Project Overview
You will work in a team to build a Hypermarket Management System in Java.
The system simulates a basic back-office system for a hypermarket or supermarket to manage:
- Products and categories
- Stock quantities
- Sales and receipts
- Simple inventory reports
Your application must:
- Be written in Java, using Object-Oriented Programming (OOP)
- Use text files for persistent storage only (no database)
- Provide a console-based UI (a GUI is optional bonus)
We will avoid the common weakness of this domain (just a procedural “product list + menu”) by
requiring multiple classes, proper encapsulation, and at least one composition relationship (Sale with
multiple SaleItems).

## 2. Learning Objectives
By completing this project, you should be able to:
- Design a class model for an inventory and sales domain (products, categories, sales, receipts).
- Implement encapsulated Java classes with constructors, getters/setters, and clear behavior
methods.
- Use composition (e.g., a Sale containing multiple SaleItems) to model real-world
relationships.
- Apply collections (ArrayList) to manage lists of products and sale items.
- Persist inventory and sales data using file I/O so that data survives across program runs.
- Explain your design decisions and OOP concepts in your own words during individual
questions.

- OOP & Technical Requirements (To Avoid Weak Designs)
To avoid the typical “flat CRUD menu + one array” weakness, your project must satisfy:
- Minimum Number of Domain Classes

o At least 5–6 domain classes, for example:
Product, Category (enum/class), Sale, SaleItem, InventoryManager (or Hypermarket),
plus optional FileManager or similar.
o A single “God class” with all fields and logic is not allowed.
- Composition (Required)
o You must model a sale/receipt as an object that contains multiple items, e.g.:
▪ class Sale
▪ class SaleItem linking a Product to a quantity and line total
o This avoids the weakness of “just decreasing stock by hand” without modeling sales
properly.
## 3. Encapsulation
o All fields must be private (or protected only with clear justification).
o No public fields.
o Use getters/setters and domain methods:
▪ increaseStock(int amount), decreaseStock(int amount)
▪ calculateTotal() in Sale
- Separation of Concerns
o Separate:
▪ Domain classes (Product, Sale, SaleItem, etc.)
▪ UI/menu handling
▪ File I/O utilities
o main should orchestrate, not contain all the logic.
- File-Based Persistence Only
o All important persistent data:
▪ Products and categories
▪ Stock quantities
▪ (At least recent) sales/receipts
o Must be stored and loaded via Java file I/O (File, Scanner, PrintWriter, etc.).
o No database or external persistence libraries.


## 4. Functional Scope
We combine Beginner + Intermediate features into the required project scope.
Advanced features are bonus only.
4.1 Core Required Features (Beginner Level)
These are the minimum features that every team must implement.
## 1. Product & Category Management
## • Product
o Fields (at least):
▪ productId or code
▪ name
▪ unitPrice
▪ stockQuantity
▪ category (string or enum initially)
o Operations:
▪ Add new product
▪ List all products
▪ Search product by code or name
## • Categories
o At Beginner level, category can be a String or a simple enum Category { FOOD, DRINKS,
## CLEANING, ELECTRONICS, OTHER }.
o You must at least store + display category for each product.
- Stock Management (Basic)
- Adjust stock when:
o New stock arrives (increase quantity)
o Items are sold (decrease quantity)
- Prevent negative stock:
o If the sale quantity > current stock, show an error and reject the sale.
- Simple Sale & Receipt (Minimum Composition)
- A basic Sale workflow:
o Start a new sale.
o Add multiple products with quantities to the sale.
o Compute total amount for the sale.

o Print a simple receipt on the console (list of items, quantities, unit price, line total, and
total price).
- This must be modeled using at least:
o Sale
o SaleItem (contains a reference to a Product and a quantity)
- Console-Based Menus
- Main menu should allow:
o Product management (add/list/search)
o Stock management (receive stock)
o New sale (which internally:
▪ Adds multiple items
▪ Computes total
▪ Updates stock
## )
- File Persistence (Required)
For final submission:
- Products + stock must be loaded from file(s) on startup and saved on exit.
- At least the last N sales (or all sales) must be stored in a file (e.g. sales.txt) with enough detail
to re-display them later.

4.2 Extended Required Features (Intermediate Level)
These extend your system and are also required (they keep the project from being trivial / pure-
## CRUD).
- Category as Enum and Filtering
- Use an enum Category for product categories instead of just strings.
## • Implement:
o List products by category (e.g., show all FOOD products).
- Low-Stock Report
- Implement a low-stock alert feature:
o For example, user can set a lowStockThreshold (e.g., 5 units).
o System can list all products with stockQuantity <= threshold.
This helps avoid the weakness where there are no meaningful reports.

- Sales Report (Minimum)
Implement at least one of:
- Daily/Session Sales Summary (per run or per file set)
o Total number of sales processed
o Total revenue (sum of sale totals)
o Optional: revenue per category
- Top-Selling Products (Basic)
o List products with highest quantities sold (since file history began or since program
start).
- File I/O (Intermediate Level)
Extend file persistence to include:
- Product list with quantity and category
## • Sales:
o Each sale with its line items (product code, quantity, line total)
o The date/time of sale can be a simple string if you wish
You may use simple text formats (e.g., CSV-like) as long as you can read/write them reliably.

4.3 Bonus Advanced Features (+ up to 5 Points)
These are optional. Only implement them after your required features are complete and stable.
A. Product Inheritance (Perishable vs Non-Perishable) (Bonus 1–3 pts)
- Introduce inheritance for product types:
o abstract class Product
o class PerishableProduct extends Product (e.g., has expiryDate)
o class NonPerishableProduct extends Product
- Perishable products:
o Store expiry date as string (e.g. "2025-12-31")
o Add a report for “near-expiry” or “expired” items.
B. Discount System (Strategy-Like Design) (Bonus 1–3 pts)
- Implement discount logic using an interface like:
o interface DiscountStrategy { double applyDiscount(double subtotal); }
- Concrete strategies:
o NoDiscount, PercentageDiscount, BuyXGetYFree (simplified)

- Allow choosing a discount strategy per sale or per category.
C. More Advanced Reports (Bonus 1–2 pts)
- Example ideas:
o Revenue per category over time
o Average sale value
o Best-selling category
D. Simple GUI (Swing/JavaFX) (Bonus 2–5 pts)
- A basic GUI interface for:
o Product management
o Creating sales
o Viewing reports
- Only attempt this after:
o OOP design is solid
o File persistence is done
o Console version is stable
⚠ A strong console + OOP solution is better than a buggy GUI. Don’t sacrifice correctness
for looks.

- Suggested Design (OOP Concepts to Demonstrate)
You are free to choose your exact design, but we recommend at least:
- class Product
o Fields: productCode, name, Category category, double unitPrice, int stockQuantity
o Methods: increaseStock(int x), decreaseStock(int x), maybe toString().
- enum Category
o Values such as FOOD, DRINKS, CLEANING, ELECTRONICS, OTHER.
- class Sale
o Fields: saleId, date/time (as string), ArrayList<SaleItem>
o Methods: addItem(Product p, int quantity), calculateTotal().
- class SaleItem
o Fields: Product product, int quantity, double lineTotal (or compute from quantity *
price).
- class InventoryManager (or Hypermarket)

o Holds:
▪ ArrayList<Product> products
▪ ArrayList<Sale> sales
o Methods: add product, find product, process sale, generate reports.
- class FileManager (optional but recommended)
o Handles reading/writing products and sales from/to files.
- class App or Main
o Handles menus, calls methods on InventoryManager.
Key OOP Concepts to show clearly:
- Classes & Objects: representing real-world elements (products, categories, sales).
- Encapsulation: private fields, getters/setters, and methods enforcing invariants (e.g., no
negative stock).
- Constructors & Methods: initialize objects properly, clean business methods (processSale(),
getLowStockProducts()).
- Composition: Sale has many SaleItems; InventoryManager has lists of Product and Sale.
- Optional Inheritance (Bonus): Product subclasses (PerishableProduct,
NonPerishableProduct).

## 6. Timeline & Milestones (2.5 Weeks)
Use this plan to manage your time. Your instructor may adjust the schedule.
## Milestone 1 – Domain Model & Class Skeleton
## • Deliverables:
o Simple class diagram:
▪ Product, Category, Sale, SaleItem, InventoryManager, FileManager, etc.
o Java classes with:
▪ Fields and constructors
▪ Empty methods for planned functionality
Milestone 2 – Core In-Memory Logic (Beginner Features)
- Implement (in memory, no persistence yet):
o Add/list/search products
o Adjust stock (add/remove)
o Create a sale and print a receipt (Sale with multiple SaleItems)

## • Deliverable:
o Working console app showing inventory and simple sales without file loading/saving
yet.
Milestone 3 – Extended OOP & Reports (Intermediate Features)
## • Implement:
o Category as enum
o Listing products by category
o Low-stock report
o At least one sales report (summary or top-selling products)
## • Deliverable:
o Demo where reports are generated from in-memory data.
## Milestone 4 – File Persistence & Finalization
## • Implement:
o Full file I/O for products and sales.
o On startup: read from files
o On exit: save to files
o Handle basic input errors gracefully
- If stable, start adding bonus features: product inheritance, discount strategies, or GUI.
## • Deliverable:
o Demo showing persistent storage working between runs.
## Milestone 5 – Final Testing & Presentation
- Thoroughly test:
o Product operations
o Sales and stock updates
o Reports
o File persistence
## • Prepare:
o Final code with comments
o Class diagram (updated)
o Short report and demo (~10 minutes)


## 7. Deliverables
By the deadline, your team must submit:
## 1. Source Code
o All .java files, logically organized.
o Clear naming and comments where needed.
## 2. Class Diagram
o At minimum: Product, Category, Sale, SaleItem, InventoryManager, and their
relationships.
## 3. Short Report (1–2 Pages)
o Summary of implemented features.
o Brief description of your design.
o List of any bonus features.
## 4. Demo / Presentation
o Show:
▪ Adding products and adjusting stock
▪ Creating sales and printing receipts
▪ Low-stock and sales reports
▪ Data persistence across runs

## 8. Individual Questions (4 Marks)
Each student will get individual questions (oral or written) about:
- Their actual contributions: which classes/methods they worked on.
- Their understanding of:
o Classes, objects, encapsulation, composition.
o Design choices in the Hypermarket system (e.g., why have SaleItem instead of just a
quantity array).
- Ability to explain or modify small parts of the code.
⚠ A student who cannot explain important parts of “their” code may lose individual marks,
even if the project works well.


## 9. Grading Scheme (20 + 5 Bonus Marks)
Project (Team) – 16 Marks
- OOP Design & Structure (Classes, Relationships, Encapsulation) – 5 marks
o Clear separation of responsibilities (no giant Main).
o Proper use of Product, Sale, SaleItem, and manager/service classes.
o Private fields, good method decomposition (no huge methods doing everything).
- Use of Composition & Collections – 3 marks
o Proper Sale–SaleItem composition.
o Effective use of ArrayList (or similar) for products and sale items.
o Clean, reusable methods for operations.
- Functionality & Correctness – 5 marks
o Required features implemented and working:
▪ Product management (add/list/search)
▪ Stock updates
▪ Sales and receipts
▪ Category-based listing
▪ Low-stock or similar report
▪ At least one sales summary
o Reasonable handling of invalid input (e.g., negative quantities, non-existing product
codes).
- File-Based Persistence & Data Handling – 2 marks
o Products and their stock persisted between runs.
o Sales persisted (at least basic details).
o File formats consistent and correctly handled.
- Code Quality & Documentation – 1 mark
o Readable, well-structured code.
o Meaningful names and helpful comments.
Individual Questions (Per Student) – 4 Marks
- Understanding of:
o Their own work.
o Key OOP ideas applied in the project.

o How data flows through the system (from input to sale to file).
Bonus Features – Up to +5 Marks
Awarded for correctly implemented and cleanly integrated advanced features:
- Inheritance for product types (PerishableProduct, NonPerishableProduct).
- Discount strategies (Strategy-like design).
- Extra meaningful reports.
- Simple GUI with proper separation from domain logic.
- Any other creative enhancements aligned with good OOP.
Bonus marks can enhance a good project, but do not compensate for missing core features or poor
design.
