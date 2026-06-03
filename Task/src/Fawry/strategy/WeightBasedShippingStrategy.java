package Fawry.strategy;

import Fawry.model.cart.Cart;

// Strategy Pattern: calculates shipping based on total weight
public class WeightBasedShippingStrategy implements ShippingStrategy {
    private static final double RATE_PER_KG = 10.0;

    @Override
    public double calculateShipping(Cart cart) {
        return cart.totalWeight() * RATE_PER_KG;
    }
}
