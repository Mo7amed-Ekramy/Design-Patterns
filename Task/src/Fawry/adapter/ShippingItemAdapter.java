package Fawry.adapter;

import Fawry.model.product.Product;
import Fawry.shipping.Shippable;

// Adapter Pattern: adapts Product + Shippable to an external shipping item shape.
public class ShippingItemAdapter implements ExternalShippingItem {
    private Product product;
    private Shippable shippable;
    private int quantity;

    public ShippingItemAdapter(Product product, int quantity) {
        if (!(product instanceof Shippable shippableProduct) || shippableProduct.getWeight() <= 0) {
            throw new IllegalArgumentException("Product is not shippable.");
        }
        this.product = product;
        this.shippable = shippableProduct;
        this.quantity = quantity;
    }

    @Override
    public String getItemName() {
        return product.getName();
    }

    @Override
    public double getItemWeight() {
        return shippable.getWeight();
    }

    @Override
    public int getQuantity() {
        return quantity;
    }
}
