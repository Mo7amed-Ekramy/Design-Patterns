package Fawry.model.product;

import Fawry.shipping.Shippable;

public class Cheese extends Product implements Shippable {
    private double weight;

    public Cheese(String name, double price, int quantity, double weight) {
        super(name, price, quantity);
        this.weight = weight;
    }

    @Override
    public boolean isExpirable() {
        return true;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
