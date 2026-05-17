package Fawry.strategy;

import Fawry.model.cart.Cart;

// Strategy Pattern: lets Cart use replaceable shipping calculation logic.
public interface ShippingStrategy {
    double calculateShipping(Cart cart);
}
