package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import model.Customer;
import model.MenuItem;
import model.Restaurant;
import service.MenuItemDAO;
import service.RestaurantDAO;
import structures.RestaurantHashTable;

public class BrowseMenu extends JFrame {

    private Customer customer;
    private JLabel lblTitle;
    private JLabel lblLocation;
    private JLabel lblRestaurants;
    private JLabel lblMenu;
    private JTextField txtLocation;
    private JButton btnSearch;
    private JButton btnViewMenu;
    private JButton btnOrderNow;
    private JButton btnBack;
    private DefaultListModel<Restaurant> restaurantModel = new DefaultListModel<>();
    private DefaultListModel<MenuItem> menuModel = new DefaultListModel<>();
    private JList<Restaurant> listRestaurants;
    private JList<MenuItem> listMenu;
    private JScrollPane scrollRestaurants;
    private JScrollPane scrollMenu;

    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private MenuItemDAO menuDAO = new MenuItemDAO();
    private RestaurantHashTable hashTable = new RestaurantHashTable(16);

    public BrowseMenu(Customer customer) {
        this.customer = customer;
        setTitle("Browse Menu");
        setSize(560, 500);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Browse Menu");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 18));
        lblTitle.setBounds(20, 15, 250, 30);
        add(lblTitle);

        lblLocation = new JLabel("Your Location:");
        lblLocation.setBounds(20, 60, 100, 25);
        add(lblLocation);

        txtLocation = new JTextField(customer.getAddress());
        txtLocation.setBounds(130, 60, 270, 25);
        add(txtLocation);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(420, 60, 100, 25);
        add(btnSearch);

        lblRestaurants = new JLabel("Restaurants:");
        lblRestaurants.setBounds(20, 100, 150, 20);
        add(lblRestaurants);

        lblMenu = new JLabel("Menu:");
        lblMenu.setBounds(290, 100, 150, 20);
        add(lblMenu);

        listRestaurants = new JList<>(restaurantModel);
        scrollRestaurants = new JScrollPane(listRestaurants);
        scrollRestaurants.setBounds(20, 125, 250, 250);
        add(scrollRestaurants);

        listMenu = new JList<>(menuModel);
        scrollMenu = new JScrollPane(listMenu);
        scrollMenu.setBounds(290, 125, 230, 250);
        add(scrollMenu);

        btnViewMenu = new JButton("View Menu");
        btnViewMenu.setBounds(20, 395, 110, 30);
        add(btnViewMenu);

        btnOrderNow = new JButton("Order Now");
        btnOrderNow.setBounds(140, 395, 110, 30);
        add(btnOrderNow);

        btnBack = new JButton("Back");
        btnBack.setBounds(410, 395, 110, 30);
        add(btnBack);

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchRestaurants();
            }
        });

        btnViewMenu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewMenu();
            }
        });

        btnOrderNow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                orderNow();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new HomePage(BrowseMenu.this.customer).setVisible(true);
                dispose();
            }
        });

        loadHashTable();
    }

    private void loadHashTable() {
        try {
            hashTable.clear();
            ArrayList<Restaurant> list = restaurantDAO.getAll();
            for (int i = 0; i < list.size(); i++) {
                hashTable.insert(list.get(i));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void searchRestaurants() {
        String location = txtLocation.getText().trim();
        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter your location.");
            return;
        }

        restaurantModel.clear();
        menuModel.clear();
        ArrayList<Restaurant> result = hashTable.getByLocation(location);
        for (int i = 0; i < result.size(); i++) {
            restaurantModel.addElement(result.get(i));
        }

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No restaurants found in " + location + ".");
        }
    }

    private void viewMenu() {
        Restaurant selected = listRestaurants.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a restaurant first.");
            return;
        }

        try {
            menuModel.clear();
            ArrayList<MenuItem> items = menuDAO.getByRestaurant(selected.getId());
            for (int i = 0; i < items.size(); i++) {
                menuModel.addElement(items.get(i));
            }
            if (items.isEmpty()) {
                JOptionPane.showMessageDialog(this, "This restaurant has no menu items yet.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void orderNow() {
        Restaurant selected = listRestaurants.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a restaurant first.");
            return;
        }
        new PlaceOrder(customer, selected).setVisible(true);
        dispose();
    }
}