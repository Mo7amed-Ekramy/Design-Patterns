package Fawry.strategy;

import Fawry.model.cart.Cart;

// Strategy Pattern: preserves the original flat shipping rule exactly.
public class FlatRateShippingStrategy implements ShippingStrategy {
    @Override
    public double calculateShipping(Cart cart) {
        if (cart.totalWeight() > 0) {
            return 30;
        }
        return 0;
    }
}
