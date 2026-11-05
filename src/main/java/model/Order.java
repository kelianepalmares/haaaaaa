package model;

import java.util.ArrayList;

public class Order {

    private int orderId;
    private int customerId;
    private int restaurantId;
    private ArrayList<OrderItem> orderItems;
    private String status;      // Preparing, Out for Delivery, Delivered
    private int urgency;        // 1 = pinaka-urgent, 5 = hindi urgent
    private int deliveryTime;   // minutes

    public Order(int orderId, int customerId, int restaurantId, String status, int urgency, int deliveryTime) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.restaurantId = restaurantId;
        this.status = status;
        this.urgency = urgency;
        this.deliveryTime = deliveryTime;
        this.orderItems = new ArrayList<>();
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }

    public ArrayList<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(ArrayList<OrderItem> orderItems) { this.orderItems = orderItems; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getUrgency() { return urgency; }
    public void setUrgency(int urgency) { this.urgency = urgency; }

    public int getDeliveryTime() { return deliveryTime; }
    public void setDeliveryTime(int deliveryTime) { this.deliveryTime = deliveryTime; }

    public void addItem(OrderItem item) {
        orderItems.add(item);
    }

    public double getTotal() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getSubtotal();
        }
        return total;
    }
}