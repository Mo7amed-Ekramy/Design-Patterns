package Fawry.singleton;

import java.util.ArrayList;
import java.util.List;

import Fawry.adapter.ExternalShippingItem;
import Fawry.adapter.ExternalShippingService;
import Fawry.adapter.ShippingItemAdapter;
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
            printError("Cart is empty. Cannot proceed to checkout.");
            return;
        }

        double subtotal = cart.calculateSubtotal();
        double shipping = cart.calculateShipping();
        double total = subtotal + shipping;

        if (customer.getBalance() < total) {
            printError("Insufficient balance. Cannot proceed.");
            return;
        }

        printHeader("Checkout for " + customer.getName());

        if (shipping > 0) {
            printSection("Shipment Notice");
            List<ExternalShippingItem> externalShippingItems = new ArrayList<>();
            for (CartItem item : cart.getItems()) {
                if (item.getProduct() instanceof Shippable shippable && shippable.getWeight() > 0) {
                    System.out.printf("%2dx %-25s %6.1f kg%n",
                            item.getQuantity(),
                            item.getProduct().getName(),
                            shippable.getWeight() * item.getQuantity());
                    externalShippingItems.add(new ShippingItemAdapter(item.getProduct(), item.getQuantity()));
                }
            }
            System.out.println("-------------------------------");
            System.out.printf("%-28s %6.1f kg%n", "Total package weight", cart.totalWeight());
            new ExternalShippingService().ship(customer.getCity(), externalShippingItems);
        }

        printSection("Checkout Receipt");
        for (CartItem item : cart.getItems()) {
            System.out.printf("%2dx %-25s %8.2f%n",
                    item.getQuantity(),
                    item.getProduct().getName(),
                    item.getProduct().getPrice() * item.getQuantity());
        }
        System.out.println("-------------------------------------");
        System.out.printf("%-28s %8.2f%n", "Subtotal", subtotal);
        System.out.printf("%-28s %8.2f%n", "Shipping", shipping);
        System.out.printf("%-28s %8.2f%n", "Amount", total);

        customer.deduct(total);
        System.out.printf("%-28s %8.2f%n", "Balance after payment", customer.getBalance());
        printFooter();
    }

    private void printHeader(String title) {
        System.out.println();
        System.out.println("=====================================");
        System.out.println(title);
        System.out.println("=====================================");
    }

    private void printSection(String title) {
        System.out.println();
        System.out.println(title);
        System.out.println("-------------------------------------");
    }

    private void printFooter() {
        System.out.println("=====================================");
        System.out.println();
    }

    private void printError(String message) {
        System.out.println();
        System.out.println("[Checkout] " + message);
    }
}
