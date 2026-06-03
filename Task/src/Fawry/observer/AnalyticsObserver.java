package Fawry.observer;

import Fawry.model.product.Product;

// Observer Pattern: second observer to demonstrate multiple listeners
public class AnalyticsObserver implements CartObserver {
    private int totalItemsAdded = 0;
    private int totalFailedAttempts = 0;

    @Override
    public void onProductAdded(Product product, int quantity) {
        totalItemsAdded += quantity;
        System.out.printf("[Analytics] Event Tracked: ADDED | Total items in cart so far: %d%n", totalItemsAdded);
    }

    @Override
    public void onAddFailed(Product product, int quantity) {
        totalFailedAttempts++;
        System.out.printf("[Analytics] Event Tracked: FAILED | Total failed attempts: %d%n", totalFailedAttempts);
    }
}
