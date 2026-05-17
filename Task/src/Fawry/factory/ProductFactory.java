package Fawry.factory;

import Fawry.model.product.Biscuits;
import Fawry.model.product.Cheese;
import Fawry.model.product.Product;
import Fawry.model.product.ScratchCard;
import Fawry.model.product.TV;

// Factory Pattern: centralizes product creation while returning the same product types.
public class ProductFactory {
    private ProductFactory() {
    }

    public static Product createCheese(String name, double price, int quantity, double weight) {
        return new Cheese(name, price, quantity, weight);
    }

    public static Product createBiscuits(String name, double price, int quantity, double weight) {
        return new Biscuits(name, price, quantity, weight);
    }

    public static Product createTV(String name, double price, int quantity, double weight) {
        return new TV(name, price, quantity, weight);
    }

    public static Product createScratchCard(String name, double price, int quantity) {
        return new ScratchCard(name, price, quantity);
    }
}
