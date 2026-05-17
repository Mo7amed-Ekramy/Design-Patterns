package Fawry.model.product;

import Fawry.shipping.Shippable;

public class TV extends Product implements Shippable {
    private double weight;

    public TV(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    @Override
    public boolean isExpirable() {
        return false;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
