package Fawry.singleton;

import Fawry.model.cart.Cart;
import Fawry.model.cart.CartItem;
import Fawry.model.customer.Customer;
import Fawry.shipping.Shippable;

// Singleton Pattern: one checkout service instance for the application.
public class CheckoutService {
    private static final CheckoutService INSTANCE = new CheckoutService();

    private CheckoutService() {
    }

    public static CheckoutService getInstance() {
        return INSTANCE;
    }

    public void checkout(Customer customer, Cart cart) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Cannot proceed to checkout.");
            return;
        }

        double subtotal = cart.calculateSubtotal();
        double shipping = cart.calculateShipping();
        double total = subtotal + shipping;

        if (customer.getBalance() < total) {
            System.out.println("Insufficient balance. Cannot proceed.");
            return;
        }

        if (shipping > 0) {
            System.out.println("** Shipment notice **");
            for (CartItem item : cart.getItems()) {
                if (item.getProduct() instanceof Shippable) {
                    System.out.println(item.getQuantity() + "x " + item.getProduct().getName());
                }
            }
            System.out.printf("Total package weight %.1fkg\n", cart.totalWeight());
        }

        System.out.println("** Checkout receipt **");
        for (CartItem item : cart.getItems()) {
            System.out.println(item.getQuantity() + "x " + item.getProduct().getName() + " "
                    + (item.getProduct().getPrice() * item.getQuantity()));
        }
        System.out.println("----------------------");
        System.out.println("Subtotal " + subtotal);
        System.out.println("Shipping " + shipping);
        System.out.println("Amount " + total);

        customer.deduct(total);
        System.out.println("Customer balance after payment: " + customer.getBalance());
    }
}
