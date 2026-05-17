package Fawry.model.customer;

public class Customer {
    private String name;
    private double balance;
    private boolean premium;
    private String city;

    public Customer(String name, double balance) {
        this(name, balance, false, "Unknown");
    }

    public Customer(String name, double balance, boolean premium, String city) {
        this.name = name;
        this.balance = balance;
        this.premium = premium;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void deduct(double amount) {
        if (amount > balance) {
            throw new IllegalArgumentException("Insufficient balance.");
        }
        balance -= amount;
    }
}
