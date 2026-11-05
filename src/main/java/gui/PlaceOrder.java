package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import model.Customer;
import model.MenuItem;
import model.OrderItem;
import model.Restaurant;
import service.MenuItemDAO;
import service.OrderDAO;
import service.RouteService;
import structures.RouteGraph;

public class PlaceOrder extends JFrame {

    private Customer customer;
    private Restaurant restaurant;

    private JLabel lblTitle;
    private JLabel lblRestaurant;
    private JLabel lblMenu;
    private JLabel lblOrder;
    private JLabel lblQuantity;
    private JLabel lblTotal;
    private JLabel lblCustomer;
    private JLabel lblUrgency;
    private JLabel lblDelivery;
    private JLabel lblRoute;
    private JTextField txtQuantity;
    private JTextField txtDelivery;
    private JComboBox<String> cmbCustomer;
    private JComboBox<String> cmbUrgency;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnPlace;
    private JButton btnBack;
    private DefaultListModel<MenuItem> menuModel = new DefaultListModel<>();
    private DefaultListModel<String> orderModel = new DefaultListModel<>();
    private JList<MenuItem> listMenu;
    private JList<String> listOrder;
    private JScrollPane scrollMenu;
    private JScrollPane scrollOrder;

    private ArrayList<OrderItem> orderItems = new ArrayList<>();
    private MenuItemDAO menuDAO = new MenuItemDAO();
    private OrderDAO orderDAO = new OrderDAO();

    public PlaceOrder(Customer customer, Restaurant restaurant) {
        this.customer = customer;
        this.restaurant = restaurant;
        setTitle("Place Order");
        setSize(600, 580);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Place Order");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 18));
        lblTitle.setBounds(20, 10, 300, 30);
        add(lblTitle);

        lblRestaurant = new JLabel("Restaurant: " + restaurant.getName() + " (" + restaurant.getLocation() + ")");
        lblRestaurant.setBounds(20, 45, 550, 25);
        add(lblRestaurant);

        lblMenu = new JLabel("Menu:");
        lblMenu.setBounds(20, 75, 100, 20);
        add(lblMenu);

        lblOrder = new JLabel("Your Order:");
        lblOrder.setBounds(310, 75, 100, 20);
        add(lblOrder);

        listMenu = new JList<>(menuModel);
        scrollMenu = new JScrollPane(listMenu);
        scrollMenu.setBounds(20, 95, 270, 150);
        add(scrollMenu);

        listOrder = new JList<>(orderModel);
        scrollOrder = new JScrollPane(listOrder);
        scrollOrder.setBounds(310, 95, 250, 150);
        add(scrollOrder);

        lblQuantity = new JLabel("Quantity:");
        lblQuantity.setBounds(20, 255, 70, 25);
        add(lblQuantity);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(90, 255, 50, 25);
        add(txtQuantity);

        btnAdd = new JButton("Add Item");
        btnAdd.setBounds(150, 255, 110, 25);
        add(btnAdd);

        btnRemove = new JButton("Remove Item");
        btnRemove.setBounds(270, 255, 120, 25);
        add(btnRemove);

        lblTotal = new JLabel("Total: P0.0");
        lblTotal.setBounds(20, 285, 400, 25);
        add(lblTotal);

        lblCustomer = new JLabel("Customer:");
        lblCustomer.setBounds(20, 320, 80, 25);
        add(lblCustomer);

        cmbCustomer = new JComboBox<>();
        cmbCustomer.addItem(customer.getName() + " - " + customer.getAddress());
        cmbCustomer.setEnabled(false);
        cmbCustomer.setBounds(110, 320, 280, 25);
        add(cmbCustomer);

        lblUrgency = new JLabel("Urgency:");
        lblUrgency.setBounds(20, 355, 80, 25);
        add(lblUrgency);

        cmbUrgency = new JComboBox<>(new String[]{"Urgent", "High", "Normal", "Low"});
        cmbUrgency.setSelectedIndex(2);
        cmbUrgency.setBounds(110, 355, 150, 25);
        add(cmbUrgency);

        lblDelivery = new JLabel("Delivery Time (minutes):");
        lblDelivery.setBounds(20, 390, 170, 25);
        add(lblDelivery);

        txtDelivery = new JTextField();
        txtDelivery.setBounds(190, 390, 60, 25);
        add(txtDelivery);

        lblRoute = new JLabel("Route: ");
        lblRoute.setBounds(20, 425, 550, 25);
        add(lblRoute);

        btnPlace = new JButton("Place Order");
        btnPlace.setBounds(20, 470, 150, 35);
        add(btnPlace);

        btnBack = new JButton("Back");
        btnBack.setBounds(190, 470, 120, 35);
        add(btnBack);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });

        btnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeItem();
            }
        });

        btnPlace.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                placeOrder();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new BrowseMenu(PlaceOrder.this.customer).setVisible(true);
                dispose();
            }
        });

        loadMenu();
        showRoute();
    }

    private void loadMenu() {
        try {
            menuModel.clear();
            ArrayList<MenuItem> items = menuDAO.getByRestaurant(restaurant.getId());
            for (int i = 0; i < items.size(); i++) {
                menuModel.addElement(items.get(i));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void showRoute() {
        try {
            String from = restaurant.getLocation();
            String to = customer.getAddress();
            if (from.equalsIgnoreCase(to)) {
                lblRoute.setText("Route: same location (0.0)");
                return;
            }
            RouteGraph graph = RouteService.buildGraph();
            ArrayList<String> path = graph.shortestPath(from, to);
            if (path.isEmpty()) {
                lblRoute.setText("Route: no route found from " + from + " to " + to);
            } else {
                lblRoute.setText("Route: " + RouteService.pathToString(path)
                        + " (" + graph.getLastDistance() + ")");
            }
        } catch (Exception ex) {
            lblRoute.setText("Route: unavailable");
        }
    }

    private void refreshOrderList() {
        orderModel.clear();
        double total = 0;
        for (int i = 0; i < orderItems.size(); i++) {
            OrderItem item = orderItems.get(i);
            orderModel.addElement(item.getMenuItem().getName() + " x" + item.getQuantity()
                    + " = P" + item.getSubtotal());
            total += item.getSubtotal();
        }
        lblTotal.setText("Total: P" + total);
    }

    private void addItem() {
        MenuItem selected = listMenu.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a menu item first.");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(txtQuantity.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number.");
            return;
        }
        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.");
            return;
        }

        boolean found = false;
        for (int i = 0; i < orderItems.size(); i++) {
            if (orderItems.get(i).getMenuItem().getId() == selected.getId()) {
                orderItems.get(i).setQuantity(orderItems.get(i).getQuantity() + quantity);
                found = true;
                break;
            }
        }
        if (!found) {
            orderItems.add(new OrderItem(selected, quantity));
        }

        txtQuantity.setText("");
        refreshOrderList();
    }

    private void removeItem() {
        int index = listOrder.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(this, "Select an item in your order first.");
            return;
        }
        orderItems.remove(index);
        refreshOrderList();
    }

    private void placeOrder() {
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Your order is empty.");
            return;
        }

        int deliveryTime;
        try {
            deliveryTime = Integer.parseInt(txtDelivery.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Delivery time must be a whole number (minutes).");
            return;
        }
        if (deliveryTime <= 0) {
            JOptionPane.showMessageDialog(this, "Delivery time must be greater than 0.");
            return;
        }

        int urgency = cmbUrgency.getSelectedIndex() + 1;

        try {
            int orderId = orderDAO.placeOrder(customer.getId(), restaurant.getId(),
                    urgency, deliveryTime, orderItems);
            JOptionPane.showMessageDialog(this, "Order placed! Your Order ID is " + orderId + ".");
            orderItems.clear();
            refreshOrderList();
            txtDelivery.setText("");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}