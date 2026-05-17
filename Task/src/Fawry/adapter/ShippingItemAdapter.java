package Fawry.adapter;

import Fawry.model.product.Product;
import Fawry.shipping.Shippable;

// Adapter Pattern: adapts Product + Shippable to an external shipping item shape.
public class ShippingItemAdapter implements ExternalShippingItem {
    private Product product;
    private Shippable shippable;

    public ShippingItemAdapter(Product product) {
        if (!(product instanceof Shippable)) {
            throw new IllegalArgumentException("Product is not shippable.");
        }
        this.product = product;
        this.shippable = (Shippable) product;
    }

    @Override
    public String getItemName() {
        return product.getName();
    }

    @Override
    public double getItemWeight() {
        return shippable.getWeight();
    }
}
