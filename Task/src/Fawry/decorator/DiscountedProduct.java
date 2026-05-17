package Fawry.decorator;

import Fawry.model.product.Product;
import Fawry.shipping.Shippable;

// Decorator Pattern: demonstrates dynamic price modification when explicitly used.
public class DiscountedProduct extends ProductDecorator implements Shippable {
    private double discountPercentage;

    public DiscountedProduct(Product product, double discountPercentage) {
        super(product);
        this.discountPercentage = discountPercentage;
    }

    @Override
    public double getPrice() {
        return product.getPrice() * (1 - discountPercentage / 100);
    }

    @Override
    public void setPrice(double price) {
        product.setPrice(price);
    }

    @Override
    public double getWeight() {
        if (product instanceof Shippable shippable) {
            return shippable.getWeight();
        }
        return 0;
    }
}
