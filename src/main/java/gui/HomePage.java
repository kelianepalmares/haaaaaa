package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import model.Customer;

public class HomePage extends JFrame {

    private Customer customer;
    private JLabel lblTitle;
    private JLabel lblSub;
    private JButton btnBrowse;
    private JButton btnAccount;
    private JButton btnTrack;
    private JButton btnAdmin;
    private JButton btnLogout;

    public HomePage(Customer customer) {
        this.customer = customer;
        setTitle("Home Page");
        setSize(420, 450);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Online Food Delivery System");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 18));
        lblTitle.setBounds(50, 20, 330, 30);
        add(lblTitle);

        lblSub = new JLabel("Hungry, " + customer.getName() + "? Order your food here!");
        lblSub.setBounds(80, 55, 300, 25);
        add(lblSub);

        btnBrowse = new JButton("Browse Menu and Order");
        btnBrowse.setBounds(50, 100, 310, 45);
        add(btnBrowse);

        btnAccount = new JButton("Customer Account");
        btnAccount.setBounds(50, 160, 310, 45);
        add(btnAccount);

        btnTrack = new JButton("Track My Order");
        btnTrack.setBounds(50, 220, 310, 45);
        add(btnTrack);

        btnAdmin = new JButton("Admin Login");
        btnAdmin.setBounds(50, 285, 310, 45);
        add(btnAdmin);

        btnLogout = new JButton("Logout");
        btnLogout.setBounds(50, 345, 310, 45);
        add(btnLogout);

        btnBrowse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new BrowseMenu(HomePage.this.customer).setVisible(true);
                dispose();
            }
        });

        btnAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new CustomerAccount(HomePage.this.customer).setVisible(true);
                dispose();
            }
        });

        btnTrack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new TrackOrder(HomePage.this.customer).setVisible(true);
                dispose();
            }
        });

        btnAdmin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminLogin().setVisible(true);
                dispose();
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