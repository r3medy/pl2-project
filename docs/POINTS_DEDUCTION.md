# Below is all the items that may deduct points

## 1. OOP Design & Structure (max 3)

- [ ] No clear Product / Category / Offer / Order / OrderItem separation (−1)
- [ ] OrderItem missing; ad hoc lists/maps used (−1)
- [ ] Responsibilities mixed (UI + pricing + stock) (−1)

## 2. Inheritance & Polymorphism (max 3)

- [ ] Roles not in User hierarchy (Admin / Inventory / Marketing / Sales) (−1)
- [ ] Discount logic hard-coded (long if ladders) (−1)
- [ ] No polymorphic discount strategy / role behavior (−1)

## 3. Functionality & Correctness (max 5)

- [ ] Orders do not update stock correctly (−2)
- [ ] Offers not applied correctly / inconsistently (−1)
- [ ] Sales / low-stock reports missing or wrong (−1)
- [ ] Crashes or wrong totals for typical orders (−1)

## 4. File-Based Persistence (max 2)

- [ ] Products / offers / orders not reliably saved / loaded (−2)
- [ ] Minor issues (attributes lost, mis-parsed) (−1)

## 5. Code Quality & Documentation (max 2)

- [ ] Pricing logic tangled / magic numbers (−1)
- [ ] Missing docs / UML for pricing & stock logic (−1)

## 6. Oral / Individual Quiz (max 5)

- [ ] Cannot explain Order–OrderItem–Product relation (−2)
- [ ] Cannot explain where / when stock is reduced (−2)
- [ ] Minor gaps only (−1)

## 7. Bonus (up to +5)

- [ ] Extra offer types / sales analytics (+1–3)
- [ ] Polished multi-screen GUI (+2–5)
