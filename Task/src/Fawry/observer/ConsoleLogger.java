package Fawry.observer;

import Fawry.model.product.Product;

// Observer Pattern: lightweight console logger kept silent by default to preserve old output.
public class ConsoleLogger implements CartObserver {
    private boolean enabled;

    public ConsoleLogger() {
        this(false);
    }

    public ConsoleLogger(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void onProductAdded(Product product, int quantity) {
        if (enabled) {
            System.out.println("Added to cart: " + quantity + "x " + product.getName());
        }
    }

    @Override
    public void onAddFailed(Product product, int quantity) {
        if (enabled) {
            System.out.println("Failed to add: " + quantity + "x " + product.getName());
        }
    }
}
