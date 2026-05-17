package Fawry.adapter;

// Adapter Pattern target interface for a possible external shipping service.
public interface ExternalShippingItem {
    String getItemName();

    double getItemWeight();

    int getQuantity();
}
