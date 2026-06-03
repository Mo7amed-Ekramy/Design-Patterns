package Fawry.model.cart;

import java.util.ArrayList;
import java.util.List;

import Fawry.model.product.Product;
import Fawry.observer.CartObserver;
import Fawry.shipping.Shippable;
import Fawry.strategy.FlatRateShippingStrategy;
import Fawry.strategy.ShippingStrategy;

public class Cart {
    private List<CartItem> items = new ArrayList<>();
    private List<CartObserver> observers = new ArrayList<>();
    private ShippingStrategy shippingStrategy;

    public Cart() {
        this(new FlatRateShippingStrategy());
    }

    public Cart(ShippingStrategy shippingStrategy) {
        this.shippingStrategy = shippingStrategy;
    }

    public void setShippingStrategy(ShippingStrategy shippingStrategy) {
        this.shippingStrategy = shippingStrategy;
    }

    public void addObserver(CartObserver observer) {
        observers.add(observer);
    }

    public void add(Product product, int quantity) {
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
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public List<CartItem> getItems() {
        return items;
    }

    public double calculateSubtotal() {
        double total = 0;
        for (CartItem item : items) {
            total += item.getProduct().getPrice() * item.getQuantity();
        }
        return total;
    }

    public double calculateShipping() {
        return shippingStrategy.calculateShipping(this);
    }

    public double totalWeight() {
        double weight = 0;
        for (CartItem item : items) {
            if (item.getProduct() instanceof Shippable shippable) {
                if (shippable.getWeight() > 0) {
                    weight += shippable.getWeight() * item.getQuantity();
                }
            }
        }
        return weight;
    }

    private void notifyProductAdded(Product product, int quantity) {
        for (CartObserver observer : observers) {
            observer.onProductAdded(product, quantity);
        }
    }

    private void notifyAddFailed(Product product, int quantity) {
        for (CartObserver observer : observers) {
            observer.onAddFailed(product, quantity);
        }
    }
}
