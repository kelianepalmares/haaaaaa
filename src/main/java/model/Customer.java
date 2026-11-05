package model;

import java.util.ArrayList;

public class Customer {

    private int id;
    private String name;
    private String address;
    private String username;
    private String password;
    private ArrayList<Order> orderHistory;

    public Customer(int id, String name, String address, String username, String password) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.username = username;
        this.password = password;
        this.orderHistory = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public ArrayList<Order> getOrderHistory() { return orderHistory; }
    public void setOrderHistory(ArrayList<Order> orderHistory) { this.orderHistory = orderHistory; }

    public void addOrder(Order order) {
        orderHistory.add(order);
    }

    @Override
    public String toString() {
        return name + " - " + address;
    }
}