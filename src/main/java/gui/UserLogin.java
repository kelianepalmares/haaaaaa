package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.Customer;
import service.CustomerDAO;

public class UserLogin extends JFrame {

    private JLabel lblTitle;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnBack;

    private CustomerDAO dao = new CustomerDAO();

    public UserLogin() {
        setTitle("Login");
        setSize(350, 380);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Login");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 20));
        lblTitle.setBounds(135, 20, 100, 30);
        add(lblTitle);

        lblUsername = new JLabel("Username");
        lblUsername.setBounds(40, 70, 100, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(40, 95, 260, 30);
        add(txtUsername);

        lblPassword = new JLabel("Password");
        lblPassword.setBounds(40, 140, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(40, 165, 260, 30);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(40, 225, 120, 35);
        add(btnLogin);

        btnBack = new JButton("Back");
        btnBack.setBounds(180, 225, 120, 35);
        add(btnBack);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StartUp().setVisible(true);
                dispose();
            }
        });
    }

    private void login() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Customer c = dao.login(username, password);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "Invalid username or password.");
            } else {
                new HomePage(c).setVisible(true);
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}