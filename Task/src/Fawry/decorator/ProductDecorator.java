package Fawry.decorator;

import Fawry.model.product.Product;

// Decorator Pattern: base wrapper for adding product behavior dynamically.
public abstract class ProductDecorator extends Product {
    protected Product product;

    public ProductDecorator(Product product) {
        super(product.getName(), product.getPrice(), product.getQuantity());
        this.product = product;
    }

    @Override
    public String getName() {
        return product.getName();
    }

    @Override
    public void setName(String name) {
        product.setName(name);
    }

    @Override
    public int getQuantity() {
        return product.getQuantity();
    }

    @Override
    public void setQuantity(int quantity) {
        product.setQuantity(quantity);
    }

    @Override
    public void reduceQuantity(int quant) {
        product.reduceQuantity(quant);
    }

    @Override
    public boolean isExpirable() {
        return product.isExpirable();
    }
}
