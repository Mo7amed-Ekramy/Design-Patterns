# Design Patterns Refactor Explanation

This project is a small Java e-commerce/cart checkout application. It started as a simple checkout system, then it was refactored to demonstrate six design patterns:

- Factory
- Strategy
- Singleton
- Observer
- Decorator
- Adapter

The project is still simple and readable. The original cart and checkout workflow is still the base of the application, but we added useful features to make all six patterns active.

## Original Project Idea

The original project had these main classes:

- `Product`
- `Cheese`
- `Biscuits`
- `TV`
- `ScratchCard`
- `Shippable`
- `Cart`
- `CartItem`
- `Customer`
- `CheckoutService`
- `Main`

The application simulates a customer adding products to a cart and then checking out.

## Original Business Rules

These rules still exist:

- Product stock is reduced immediately when `cart.add(product, quantity)` succeeds.
- If requested quantity is greater than available stock, the item is not added.
- A failed add means the product quantity is not reduced.
- `Cheese`, `Biscuits`, and `TV` are shippable.
- `ScratchCard` is not shippable.
- Shipping is `30` if the cart has at least one shippable product.
- Shipping is `0` if the cart has no shippable products.
- Shipping cost does not depend on the actual weight amount.
- Weight is used only for shipment information.
- Checkout validates empty cart.
- Checkout validates insufficient customer balance.
- Checkout prints shipment details, receipt details, subtotal, shipping, amount, and customer balance after payment.

## New Features Added

After the refactor, we added these useful behaviors:

- Observer logging for cart events.
- Premium customer discount using Decorator.
- Customer city.
- Simulated external shipping service using Adapter.
- Cleaner formatted console output.

Ali is now a premium customer, so his products receive a 10% discount.

Mohamed is not premium, so his totals remain the same as the original behavior.

## Package Structure

The project is now organized like this:

```text
Fawry/
|-- Main.java
|-- model/
|   |-- product/
|   |   |-- Product.java
|   |   |-- Cheese.java
|   |   |-- Biscuits.java
|   |   |-- TV.java
|   |   `-- ScratchCard.java
|   |-- cart/
|   |   |-- Cart.java
|   |   `-- CartItem.java
|   `-- customer/
|       `-- Customer.java
|-- factory/
|   `-- ProductFactory.java
|-- strategy/
|   |-- ShippingStrategy.java
|   `-- FlatRateShippingStrategy.java
|-- singleton/
|   `-- CheckoutService.java
|-- observer/
|   |-- CartObserver.java
|   `-- ConsoleLogger.java
|-- decorator/
|   |-- ProductDecorator.java
|   `-- DiscountedProduct.java
|-- adapter/
|   |-- ExternalShippingItem.java
|   |-- ShippingItemAdapter.java
|   `-- ExternalShippingService.java
`-- shipping/
    `-- Shippable.java
```

## Product Model

`Product` is the abstract base class for all products.

It stores:

```java
private String name;
private double price;
private int quantity;
```

It has common methods like:

```java
getName()
getPrice()
getQuantity()
reduceQuantity(int quant)
isExpirable()
```

`reduceQuantity` keeps the original stock behavior:

```java
public void reduceQuantity(int quant) {
    if (quant > quantity) {
        throw new IllegalArgumentException("Insufficient stock.");
    }
    quantity -= quant;
}
```

Concrete products:

- `Cheese` extends `Product` and implements `Shippable`
- `Biscuits` extends `Product` and implements `Shippable`
- `TV` extends `Product` and implements `Shippable`
- `ScratchCard` extends `Product`

## Shipping Model

`Shippable` is an interface:

```java
public interface Shippable {
    double getWeight();
}
```

Any product that can be shipped implements this interface.

`ScratchCard` does not implement it, so it is not included in shipment weight.

## Cart Model

`CartItem` stores:

```java
private Product product;
private int quantity;
```

`Cart` stores:

```java
private List<CartItem> items = new ArrayList<>();
```

The cart add logic still follows the original behavior:

```java
if (quantity > product.getQuantity()) {
    if (observers.isEmpty()) {
        System.out.println("Not enough stock for: " + product.getName());
    }
    notifyAddFailed(product, quantity);
    return;
}

items.add(new CartItem(product, quantity));
product.reduceQuantity(quantity);
notifyProductAdded(product, quantity);
```

The only formatting change is that when observer logging is enabled, the observer prints the failed add message in a cleaner format.

## Customer Model

Originally, `Customer` had:

```java
private String name;
private double balance;
```

Now it also has:

```java
private boolean premium;
private String city;
```

These fields are used for the new features:

- `premium`: applies a 10% discount through the Decorator pattern.
- `city`: is sent to the external shipping service through the Adapter pattern.

Example:

```java
Customer customer = new Customer("Ali", 1000, true, "Cairo");
Customer customer2 = new Customer("mohamed", 1500, false, "Alexandria");
```

## 1. Factory Pattern

File:

```text
Fawry/factory/ProductFactory.java
```

Purpose:

Centralize product creation.

Before the refactor:

```java
Product cheese = new Cheese("Cheese 400g", 100, 10, 0.4);
```

After the refactor:

```java
Product cheese = ProductFactory.createCheese("Cheese 400g", 100, 10, 0.4);
```

The factory is simple:

```java
public static Product createCheese(String name, double price, int quantity, double weight) {
    return new Cheese(name, price, quantity, weight);
}
```

Why it is useful:

- `Main` does not directly call product constructors.
- Product creation is centralized in one place.
- Adding new product creation methods later is easy.

## 2. Strategy Pattern

Files:

```text
Fawry/strategy/ShippingStrategy.java
Fawry/strategy/FlatRateShippingStrategy.java
```

Purpose:

Move shipping calculation outside `Cart`.

`Cart` now uses:

```java
private ShippingStrategy shippingStrategy;
```

and:

```java
public double calculateShipping() {
    return shippingStrategy.calculateShipping(this);
}
```

The current strategy is `FlatRateShippingStrategy`:

```java
public double calculateShipping(Cart cart) {
    if (cart.totalWeight() > 0) {
        return 30;
    }
    return 0;
}
```

This preserves the original shipping rule:

- shippable items exist: shipping is `30`
- no shippable items: shipping is `0`

Why it is useful:

- Shipping rules are separated from `Cart`.
- We can later add another shipping rule without rewriting cart logic.

## 3. Singleton Pattern

File:

```text
Fawry/singleton/CheckoutService.java
```

Purpose:

Use one shared checkout service instance.

Before:

```java
CheckoutService.checkout(customer, cart);
```

Now:

```java
CheckoutService.getInstance().checkout(customer, cart);
```

Implementation:

```java
private static final CheckoutService INSTANCE = new CheckoutService();

private CheckoutService() {
}

public static CheckoutService getInstance() {
    return INSTANCE;
}
```

Why it is useful:

- There is only one checkout service object.
- Checkout behavior is centralized.
- It demonstrates Singleton without adding framework complexity.

## 4. Observer Pattern

Files:

```text
Fawry/observer/CartObserver.java
Fawry/observer/ConsoleLogger.java
```

Purpose:

Allow other classes to react to cart events without changing the cart business logic.

`CartObserver` defines:

```java
void onProductAdded(Product product, int quantity);
void onAddFailed(Product product, int quantity);
```

`Cart` keeps a list of observers:

```java
private List<CartObserver> observers = new ArrayList<>();
```

When adding succeeds:

```java
notifyProductAdded(product, quantity);
```

When adding fails:

```java
notifyAddFailed(product, quantity);
```

In `Main`, observer logging is enabled:

```java
cart.addObserver(new ConsoleLogger(true));
```

Example output:

```text
[Cart] Added   |  2x Cheese 400g
[Cart] Failed  |  5x TV                        | Not enough stock
```

Why it is useful:

- Cart does not need to know how logging works.
- We can later add another observer, like email notification or analytics.

## 5. Decorator Pattern

Files:

```text
Fawry/decorator/ProductDecorator.java
Fawry/decorator/DiscountedProduct.java
```

Purpose:

Add new behavior to products dynamically without changing the original product classes.

We use it for premium customer discounts.

In `Main`:

```java
private static Product applyPremiumDiscount(Customer customer, Product product) {
    if (customer.isPremium()) {
        return new DiscountedProduct(product, 10);
    }
    return product;
}
```

Ali is premium:

```java
Customer customer = new Customer("Ali", 1000, true, "Cairo");
```

So Ali's products are wrapped:

```java
cart.add(applyPremiumDiscount(customer, cheese), 2);
cart.add(applyPremiumDiscount(customer, biscuits), 1);
cart.add(applyPremiumDiscount(customer, scratchCard), 1);
```

`DiscountedProduct` changes the price:

```java
@Override
public double getPrice() {
    return product.getPrice() * (1 - discountPercentage / 100);
}
```

Important detail:

`DiscountedProduct` also implements `Shippable`, so discounted shippable products still count in shipping:

```java
@Override
public double getWeight() {
    if (product instanceof Shippable shippable) {
        return shippable.getWeight();
    }
    return 0;
}
```

Why it is useful:

- We can discount products without changing `Cheese`, `Biscuits`, `TV`, or `ScratchCard`.
- The discount is dynamic and applied only when needed.

## 6. Adapter Pattern

Files:

```text
Fawry/adapter/ExternalShippingItem.java
Fawry/adapter/ShippingItemAdapter.java
Fawry/adapter/ExternalShippingService.java
```

Purpose:

Connect our internal product model to a simulated external shipping service that expects a different format.

Internally, our project has:

```java
Product.getName()
Shippable.getWeight()
CartItem.getQuantity()
Customer.getCity()
```

The external service expects:

```java
ExternalShippingItem
```

with:

```java
String getItemName();
double getItemWeight();
int getQuantity();
```

`ShippingItemAdapter` converts our product to the external format:

```java
externalShippingItems.add(new ShippingItemAdapter(item.getProduct(), item.getQuantity()));
```

Then `CheckoutService` sends it:

```java
new ExternalShippingService().ship(customer.getCity(), externalShippingItems);
```

Example output:

```text
External Shipping Service
-------------------------
Destination: Cairo
 2x Cheese 400g                  0.8 kg
 1x Biscuits 700g                0.7 kg
```

Why it is useful:

- We do not change `Product` just to match another system.
- The adapter translates between our classes and the external service interface.

## Main.java Flow

`Main` now demonstrates the patterns clearly.

Factory creates products:

```java
Product cheese = ProductFactory.createCheese("Cheese 400g", 100, 10, 0.4);
Product biscuits = ProductFactory.createBiscuits("Biscuits 700g", 150, 5, 0.7);
Product tv = ProductFactory.createTV("TV", 3000, 2, 5.0);
Product scratchCard = ProductFactory.createScratchCard("Mobile scratch card", 50, 100);
```

Customers are created:

```java
Customer customer = new Customer("Ali", 1000, true, "Cairo");
Customer customer2 = new Customer("mohamed", 1500, false, "Alexandria");
```

Observer is attached:

```java
cart.addObserver(new ConsoleLogger(true));
```

Premium discount is applied for Ali:

```java
cart.add(applyPremiumDiscount(customer, cheese), 2);
```

Checkout uses Singleton:

```java
CheckoutService.getInstance().checkout(customer, cart);
```

Adapter is used inside checkout when sending shippable items to the external shipping service.

## Current Output Behavior

For Ali:

- premium customer
- city is Cairo
- receives 10% discount
- subtotal becomes `360.00`
- shipping remains `30.00`
- amount becomes `390.00`
- final balance becomes `610.00`

Ali calculation:

```text
Cheese:       2 * 100 = 200 -> 180
Biscuits:     1 * 150 = 150 -> 135
Scratch card: 1 * 50  = 50  -> 45
Subtotal: 360
Shipping: 30
Amount: 390
Balance: 1000 - 390 = 610
```

For Mohamed:

- non-premium customer
- city is Alexandria
- no discount
- subtotal remains `400.00`
- shipping remains `30.00`
- amount remains `430.00`
- final balance becomes `1070.00`

Mohamed calculation:

```text
Cheese:       2 * 100 = 200
Biscuits:     1 * 150 = 150
Scratch card: 1 * 50  = 50
Subtotal: 400
Shipping: 30
Amount: 430
Balance: 1500 - 430 = 1070
```

TV behavior is unchanged:

- TV stock is `2`
- adding `5` TVs fails
- TV is not added to either cart

## Example Console Output

```text
[Cart] Added   |  2x Cheese 400g
[Cart] Added   |  1x Biscuits 700g
[Cart] Added   |  1x Mobile scratch card
[Cart] Failed  |  5x TV                        | Not enough stock

=====================================
Checkout for Ali
=====================================

Shipment Notice
-------------------------------------
 2x Cheese 400g                  0.8 kg
 1x Biscuits 700g                0.7 kg
-------------------------------
Total package weight            1.5 kg

External Shipping Service
-------------------------
Destination: Cairo
 2x Cheese 400g                  0.8 kg
 1x Biscuits 700g                0.7 kg

Checkout Receipt
-------------------------------------
 2x Cheese 400g                 180.00
 1x Biscuits 700g               135.00
 1x Mobile scratch card          45.00
-------------------------------------
Subtotal                       360.00
Shipping                        30.00
Amount                         390.00
Balance after payment          610.00
=====================================
```

## Pattern Summary

| Pattern | Files | Current Use |
|---|---|---|
| Factory | `ProductFactory` | Creates products in `Main` |
| Strategy | `ShippingStrategy`, `FlatRateShippingStrategy` | Calculates shipping |
| Singleton | `CheckoutService` | Provides one checkout service instance |
| Observer | `CartObserver`, `ConsoleLogger` | Logs cart add success/failure |
| Decorator | `ProductDecorator`, `DiscountedProduct` | Adds premium discount dynamically |
| Adapter | `ExternalShippingItem`, `ShippingItemAdapter`, `ExternalShippingService` | Sends shippable items to an external shipping format |

## Final Notes

The project still follows the original e-commerce checkout idea, but now each design pattern has a clear purpose:

- Factory handles object creation.
- Strategy handles shipping calculation.
- Singleton handles checkout service access.
- Observer handles cart event logging.
- Decorator handles premium discounts.
- Adapter handles communication with a simulated external shipping service.
