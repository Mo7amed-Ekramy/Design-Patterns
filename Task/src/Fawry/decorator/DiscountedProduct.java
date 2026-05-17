package Fawry.decorator;

import Fawry.model.product.Product;

// Decorator Pattern: demonstrates dynamic price modification when explicitly used.
public class DiscountedProduct extends ProductDecorator {
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
}
