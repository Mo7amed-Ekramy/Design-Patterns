package Fawry;

import Fawry.observer.AnalyticsObserver;
import Fawry.strategy.WeightBasedShippingStrategy;
import Fawry.factory.ProductFactory;
import Fawry.decorator.DiscountedProduct;
import Fawry.model.cart.Cart;
import Fawry.model.customer.Customer;
import Fawry.model.product.Product;
import Fawry.observer.ConsoleLogger;
import Fawry.singleton.CheckoutService;

public class Main {
    public static void main(String[] args) {
        // Factory Pattern: product creation is centralized here.
        Product cheese = ProductFactory.createCheese("Cheese 400g", 100, 10, 0.4);
        Product biscuits = ProductFactory.createBiscuits("Biscuits 700g", 150, 5, 0.7);
        Product tv = ProductFactory.createTV("TV", 3000, 2, 5.0);
        Product scratchCard = ProductFactory.createScratchCard("Mobile scratch card", 50, 100);

        Customer customer = new Customer("Ali", 1000, true, "Cairo");

        Cart cart = new Cart();
        // Observer Pattern: dynamically attaching multiple observers.
        cart.addObserver(new ConsoleLogger(true));
        cart.addObserver(new AnalyticsObserver());
        
        cart.add(applyPremiumDiscount(customer, cheese), 2);
        cart.add(applyPremiumDiscount(customer, biscuits), 1);
        cart.add(applyPremiumDiscount(customer, scratchCard), 1);
        cart.add(tv, 5);

        // Singleton Pattern: checkout is done through one service instance.
        CheckoutService.getInstance().checkout(customer, cart);

        Customer customer2 = new Customer("mohamed", 7500, false, "Alexandria");

        Cart cart2 = new Cart();
        cart2.addObserver(new ConsoleLogger(true));
        cart2.add(cheese, 2);
        cart2.add(biscuits, 1);
        cart2.add(scratchCard, 1);
        cart2.add(tv, 2);

        // Strategy Pattern: dynamically swap the shipping strategy before checkout
        System.out.println("\n[Strategy] Swapping shipping strategy to WeightBasedShippingStrategy for mohamed.");
        cart2.setShippingStrategy(new WeightBasedShippingStrategy());

        CheckoutService.getInstance().checkout(customer2, cart2);
    }

    private static Product applyPremiumDiscount(Customer customer, Product product) {
        if (customer.isPremium()) {
            return new DiscountedProduct(product, 10);
        }
        return product;
    }
}

