package Fawry.adapter;

import java.util.List;

// Adapter Pattern demo target: simulates a shipping company API with its own item format.
public class ExternalShippingService {
    public void ship(String city, List<ExternalShippingItem> items) {
        System.out.println();
        System.out.println("External Shipping Service");
        System.out.println("-------------------------");
        System.out.println("Destination: " + city);
        for (ExternalShippingItem item : items) {
            System.out.printf("%2dx %-25s %6.1f kg%n",
                    item.getQuantity(),
                    item.getItemName(),
                    item.getItemWeight() * item.getQuantity());
        }
    }
}
