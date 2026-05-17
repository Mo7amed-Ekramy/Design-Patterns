package Fawry.observer;

import Fawry.model.product.Product;

// Observer Pattern: observers can react to cart events without changing cart logic.
public interface CartObserver {
    void onProductAdded(Product product, int quantity);

    void onAddFailed(Product product, int quantity);
}
