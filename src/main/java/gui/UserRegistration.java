package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import service.CustomerDAO;
import service.LocationDAO;

public class UserRegistration extends JFrame {

    private JLabel lblTitle;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblConfirm;
    private JLabel lblAddress;
    private JLabel lblLocation;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirm;
    private JButton btnSelectLocation;
    private JButton btnRegister;
    private JButton btnBack;

    private CustomerDAO customerDAO = new CustomerDAO();
    private LocationDAO locationDAO = new LocationDAO();
    private String selectedLocation = "";

    public UserRegistration() {
        setTitle("Sign up");
        setSize(350, 520);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Create an Account");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 16));
        lblTitle.setBounds(95, 20, 200, 30);
        add(lblTitle);

        lblUsername = new JLabel("Enter Username");
        lblUsername.setBounds(40, 65, 150, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(40, 90, 260, 30);
        add(txtUsername);

        lblPassword = new JLabel("Enter Password");
        lblPassword.setBounds(40, 130, 150, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(40, 155, 260, 30);
        add(txtPassword);

        lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setBounds(40, 195, 150, 25);
        add(lblConfirm);

        txtConfirm = new JPasswordField();
        txtConfirm.setBounds(40, 220, 260, 30);
        add(txtConfirm);

        lblAddress = new JLabel("Address");
        lblAddress.setBounds(40, 260, 150, 25);
        add(lblAddress);

        btnSelectLocation = new JButton("Select Location");
        btnSelectLocation.setBounds(40, 285, 260, 30);
        add(btnSelectLocation);

        lblLocation = new JLabel("Location: ");
        lblLocation.setBounds(40, 325, 260, 25);
        add(lblLocation);

        btnRegister = new JButton("Register");
        btnRegister.setBounds(40, 380, 120, 35);
        add(btnRegister);

        btnBack = new JButton("Back");
        btnBack.setBounds(180, 380, 120, 35);
        add(btnBack);

        btnSelectLocation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selectLocation();
            }
        });

        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StartUp().setVisible(true);
                dispose();
            }
        });
    }

    private void selectLocation() {
        try {
            ArrayList<String> locations = locationDAO.getAllLocations();
            if (locations.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No locations available yet. Ask the admin to add locations.");
                return;
            }
            String choice = (String) JOptionPane.showInputDialog(this, "Choose your location:",
                    "Select Location", JOptionPane.PLAIN_MESSAGE, null,
                    locations.toArray(), locations.get(0));
            if (choice != null) {
                selectedLocation = choice;
                lblLocation.setText("Location: " + choice);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void register() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());

        if (username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        if (!password.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }
        if (selectedLocation.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select your location.");
            return;
        }

        try {
            if (customerDAO.add(username, selectedLocation, username, password)) {
                JOptionPane.showMessageDialog(this, "Account created! You can now login.");
                new StartUp().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Duplicate entry: username already exists.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}