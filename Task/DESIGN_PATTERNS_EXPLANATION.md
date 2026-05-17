# Design Patterns Refactor Explanation

This project is a small Java e-commerce/cart checkout application. It was refactored to demonstrate six design patterns while preserving the original business behavior.

The main goal was not to change how the program works. The goal was to organize the code better and show where each pattern can fit.

## Original Business Behavior

The refactor preserves these rules:

- Product stock is reduced immediately when `cart.add(product, quantity)` succeeds.
- If requested quantity is greater than available stock, the program prints:

```text
Not enough stock for: PRODUCT_NAME
```

- The item is not added to the cart when stock is not enough.
- Shipping is `30` if the cart contains at least one shippable product.
- Shipping is `0` if the cart contains no shippable products.
- Shipping price does not depend on the actual weight amount.
- Weight is only used when printing the shipment notice.
- Checkout still prints the shipment notice, receipt, subtotal, shipping, amount, and customer balance after payment.

## Package Structure

The project was reorganized into this structure:

```text
Fawry/
├── Main.java
├── model/
│   ├── product/
│   ├── cart/
│   └── customer/
├── factory/
├── strategy/
├── singleton/
├── observer/
├── decorator/
├── adapter/
└── shipping/
```

This makes the design patterns easier to identify and discuss.

## Model Classes

The core business classes are inside the `model` package.

Product classes:

```text
Fawry/model/product/
├── Product.java
├── Cheese.java
├── Biscuits.java
├── TV.java
└── ScratchCard.java
```

`Product` is still the abstract parent class. It contains:

```java
private String name;
private double price;
private int quantity;
```

It also still contains:

```java
public void reduceQuantity(int quant)
```

This method preserves the original stock reduction behavior.

`Cheese`, `Biscuits`, and `TV` are shippable products.

`ScratchCard` is not shippable.

## Shipping Interface

The `Shippable` interface is now located in:

```text
Fawry/shipping/Shippable.java
```

It still contains only:

```java
double getWeight();
```

Any product that can be shipped implements this interface.

## Cart and CartItem

Cart classes are now inside:

```text
Fawry/model/cart/
```

`CartItem` stores:

```java
private Product product;
private int quantity;
```

`Cart` still stores a list of cart items.

The important add logic is preserved:

```java
if (quantity > product.getQuantity()) {
    System.out.println("Not enough stock for: " + product.getName());
    notifyAddFailed(product, quantity);
    return;
}

items.add(new CartItem(product, quantity));
product.reduceQuantity(quantity);
notifyProductAdded(product, quantity);
```

So stock is still reduced immediately after the item is added successfully.

## 1. Factory Pattern

File:

```text
Fawry/factory/ProductFactory.java
```

Purpose:

Centralize product creation.

Before:

```java
Product cheese = new Cheese("Cheese 400g", 100, 10, 0.4);
```

After:

```java
Product cheese = ProductFactory.createCheese("Cheese 400g", 100, 10, 0.4);
```

The factory methods simply return the correct product objects:

```java
public static Product createCheese(String name, double price, int quantity, double weight) {
    return new Cheese(name, price, quantity, weight);
}
```

This keeps `Main` cleaner and hides direct constructor usage.

Business behavior changed: **No**.

## 2. Strategy Pattern

Files:

```text
Fawry/strategy/ShippingStrategy.java
Fawry/strategy/FlatRateShippingStrategy.java
```

Purpose:

Move shipping calculation outside `Cart`.

`Cart` now delegates shipping calculation:

```java
public double calculateShipping() {
    return shippingStrategy.calculateShipping(this);
}
```

The current strategy preserves the original rule:

```java
public class FlatRateShippingStrategy implements ShippingStrategy {
    @Override
    public double calculateShipping(Cart cart) {
        if (cart.totalWeight() > 0) {
            return 30;
        }
        return 0;
    }
}
```

This means:

- if total shippable weight is greater than `0`, shipping is `30`
- otherwise, shipping is `0`

Business behavior changed: **No**.

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

After:

```java
CheckoutService.getInstance().checkout(customer, cart);
```

The singleton implementation:

```java
private static final CheckoutService INSTANCE = new CheckoutService();

private CheckoutService() {
}

public static CheckoutService getInstance() {
    return INSTANCE;
}
```

The checkout logic itself is the same:

- check if cart is empty
- calculate subtotal
- calculate shipping
- calculate total
- check customer balance
- print shipment notice
- print checkout receipt
- deduct customer balance

Business behavior changed: **No**.

## 4. Observer Pattern

Files:

```text
Fawry/observer/CartObserver.java
Fawry/observer/ConsoleLogger.java
```

Purpose:

Allow other objects to react to cart events without changing cart logic.

`CartObserver` defines two events:

```java
void onProductAdded(Product product, int quantity);

void onAddFailed(Product product, int quantity);
```

`Cart` has:

```java
private List<CartObserver> observers = new ArrayList<>();
```

and:

```java
public void addObserver(CartObserver observer) {
    observers.add(observer);
}
```

When adding succeeds:

```java
notifyProductAdded(product, quantity);
```

When adding fails:

```java
notifyAddFailed(product, quantity);
```

`ConsoleLogger` is registered in `Main`, but it is silent by default:

```java
cart.addObserver(new ConsoleLogger());
```

This keeps the original console output unchanged.

Business behavior changed: **No**.

## 5. Decorator Pattern

Files:

```text
Fawry/decorator/ProductDecorator.java
Fawry/decorator/DiscountedProduct.java
```

Purpose:

Allow product behavior to be modified dynamically without changing the original product class.

Example future usage:

```java
Product discountedCheese = new DiscountedProduct(cheese, 10);
```

This would apply a 10% discount.

`DiscountedProduct` changes the price dynamically:

```java
@Override
public double getPrice() {
    return product.getPrice() * (1 - discountPercentage / 100);
}
```

Important:

The decorator is not used in `Main` by default because using it would change prices and totals.

Business behavior changed: **No**.

## 6. Adapter Pattern

Files:

```text
Fawry/adapter/ExternalShippingItem.java
Fawry/adapter/ShippingItemAdapter.java
```

Purpose:

Adapt the current product and shipping model to a possible external shipping format.

The external format expects:

```java
String getItemName();

double getItemWeight();
```

But the current project has:

```java
Product.getName()
Shippable.getWeight()
```

So `ShippingItemAdapter` converts a `Product` that implements `Shippable` into the external shape:

```java
public class ShippingItemAdapter implements ExternalShippingItem
```

It exposes:

```java
public String getItemName() {
    return product.getName();
}

public double getItemWeight() {
    return shippable.getWeight();
}
```

Important:

The adapter is not used in the checkout logic by default. It exists to demonstrate how the current model could integrate with another shipping API.

Business behavior changed: **No**.

## Main.java After Refactor

`Main` now demonstrates the active patterns clearly.

Factory usage:

```java
Product cheese = ProductFactory.createCheese("Cheese 400g", 100, 10, 0.4);
Product biscuits = ProductFactory.createBiscuits("Biscuits 700g", 150, 5, 0.7);
Product tv = ProductFactory.createTV("TV", 3000, 2, 5.0);
Product scratchCard = ProductFactory.createScratchCard("Mobile scratch card", 50, 100);
```

Observer usage:

```java
Cart cart = new Cart();
cart.addObserver(new ConsoleLogger());
```

Singleton usage:

```java
CheckoutService.getInstance().checkout(customer, cart);
```

Decorator and Adapter are intentionally not used in `Main`, because the goal is to preserve the original checkout totals and console output.

## Final Behavior

The sample still behaves the same:

- adding `5` TVs fails because TV stock is only `2`
- TV is not added to the cart
- shipping remains `30`
- subtotal remains `400`
- total amount remains `430`
- first customer balance becomes `570`
- second customer balance becomes `1070`

Example output:

```text
Not enough stock for: TV
** Shipment notice **
2x Cheese 400g
1x Biscuits 700g
Total package weight 1.5kg
** Checkout receipt **
2x Cheese 400g 200.0
1x Biscuits 700g 150.0
1x Mobile scratch card 50.0
----------------------
Subtotal 400.0
Shipping 30.0
Amount 430.0
Customer balance after payment: 570.0
```

## Summary

The refactor added these design patterns:

| Pattern | Where It Is Used | Active In Current Flow |
|---|---|---|
| Factory | `ProductFactory` | Yes |
| Strategy | `ShippingStrategy`, `FlatRateShippingStrategy` | Yes |
| Singleton | `CheckoutService` | Yes |
| Observer | `CartObserver`, `ConsoleLogger` | Yes, but silent |
| Decorator | `ProductDecorator`, `DiscountedProduct` | Available, not used |
| Adapter | `ExternalShippingItem`, `ShippingItemAdapter` | Available, not used |

The main idea:

The project now demonstrates six design patterns in a clean educational way, while keeping the original e-commerce/cart checkout behavior unchanged.
