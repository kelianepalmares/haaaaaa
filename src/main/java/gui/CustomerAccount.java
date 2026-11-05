package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.Customer;
import service.CustomerDAO;
import service.LocationDAO;

public class CustomerAccount extends JFrame {

    private Customer customer;
    private JLabel lblTitle;
    private JLabel lblUsername;
    private JLabel lblName;
    private JLabel lblAddress;
    private JLabel lblPassword;
    private JTextField txtUsername;
    private JTextField txtName;
    private JComboBox<String> cmbAddress;
    private JPasswordField txtPassword;
    private JButton btnSave;
    private JButton btnBack;

    private CustomerDAO customerDAO = new CustomerDAO();
    private LocationDAO locationDAO = new LocationDAO();

    public CustomerAccount(Customer customer) {
        this.customer = customer;
        setTitle("Customer Account");
        setSize(380, 430);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("My Account");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 18));
        lblTitle.setBounds(110, 15, 200, 30);
        add(lblTitle);

        lblUsername = new JLabel("Username");
        lblUsername.setBounds(40, 60, 150, 25);
        add(lblUsername);

        txtUsername = new JTextField(customer.getUsername());
        txtUsername.setEditable(false);
        txtUsername.setBounds(40, 85, 280, 28);
        add(txtUsername);

        lblName = new JLabel("Name");
        lblName.setBounds(40, 120, 150, 25);
        add(lblName);

        txtName = new JTextField(customer.getName());
        txtName.setBounds(40, 145, 280, 28);
        add(txtName);

        lblAddress = new JLabel("Address (Location)");
        lblAddress.setBounds(40, 180, 150, 25);
        add(lblAddress);

        cmbAddress = new JComboBox<>();
        cmbAddress.setBounds(40, 205, 280, 28);
        add(cmbAddress);

        lblPassword = new JLabel("New Password (leave blank to keep)");
        lblPassword.setBounds(40, 240, 280, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(40, 265, 280, 28);
        add(txtPassword);

        btnSave = new JButton("Save Changes");
        btnSave.setBounds(40, 320, 135, 35);
        add(btnSave);

        btnBack = new JButton("Back");
        btnBack.setBounds(185, 320, 135, 35);
        add(btnBack);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                save();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new HomePage(CustomerAccount.this.customer).setVisible(true);
                dispose();
            }
        });

        loadLocations();
    }

    private void loadLocations() {
        try {
            ArrayList<String> locations = locationDAO.getAllLocations();
            for (int i = 0; i < locations.size(); i++) {
                cmbAddress.addItem(locations.get(i));
            }
            cmbAddress.setSelectedItem(customer.getAddress());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void save() {
        String name = txtName.getText().trim();
        String address = (String) cmbAddress.getSelectedItem();
        String password = new String(txtPassword.getPassword());

        if (name.isEmpty() || address == null) {
            JOptionPane.showMessageDialog(this, "Name and address are required.");
            return;
        }
        if (password.isEmpty()) {
            password = customer.getPassword();
        }

        try {
            if (customerDAO.update(customer.getId(), name, address, customer.getUsername(), password)) {
                customer.setName(name);
                customer.setAddress(address);
                customer.setPassword(password);
                txtPassword.setText("");
                JOptionPane.showMessageDialog(this, "Account updated.");
            } else {
                JOptionPane.showMessageDialog(this, "Duplicate entry.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}
