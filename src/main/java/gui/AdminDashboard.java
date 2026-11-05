package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class AdminDashboard extends JFrame {

    private JLabel lblTitle;
    private JButton btnRestaurants;
    private JButton btnMenuItems;
    private JButton btnCustomers;
    private JButton btnLocations;
    private JButton btnOrderQueue;
    private JButton btnReports;
    private JButton btnLogout;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(400, 520);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Admin Dashboard");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 20));
        lblTitle.setBounds(95, 20, 250, 30);
        add(lblTitle);

        btnRestaurants = new JButton("Manage Restaurants");
        btnRestaurants.setBounds(50, 70, 290, 40);
        add(btnRestaurants);

        btnMenuItems = new JButton("Manage Menu Items");
        btnMenuItems.setBounds(50, 120, 290, 40);
        add(btnMenuItems);

        btnCustomers = new JButton("Manage Customers");
        btnCustomers.setBounds(50, 170, 290, 40);
        add(btnCustomers);

        btnLocations = new JButton("Locations and Routes");
        btnLocations.setBounds(50, 220, 290, 40);
        add(btnLocations);

        btnOrderQueue = new JButton("Order Queue");
        btnOrderQueue.setBounds(50, 270, 290, 40);
        add(btnOrderQueue);

        btnReports = new JButton("Reports");
        btnReports.setBounds(50, 320, 290, 40);
        add(btnReports);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(50, 390, 290, 40);
        add(btnLogout);

        btnRestaurants.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RestaurantForm().setVisible(true);
            }
        });

        btnMenuItems.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new MenuItemForm().setVisible(true);
            }
        });

        btnCustomers.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CustomerForm().setVisible(true);
            }
        });

        btnLocations.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LocationForm().setVisible(true);
            }
        });

        btnOrderQueue.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OrderQueueForm().setVisible(true);
            }
        });

        btnReports.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new ReportsForm().setVisible(true);
            }
        });

        btnLogout.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StartUp().setVisible(true);
                dispose();
            }
        });
    }
}