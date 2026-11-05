package gui;

import database.DBConnection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class AdminLogin extends JFrame {

    private JLabel lblTitle;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnBack;

    public AdminLogin() {
        setTitle("Admin Login");
        setSize(350, 330);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Admin Login");
        lblTitle.setBounds(125, 20, 150, 30);
        add(lblTitle);

        lblUsername = new JLabel("Username");
        lblUsername.setBounds(40, 70, 100, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(40, 95, 260, 30);
        add(txtUsername);

        lblPassword = new JLabel("Password");
        lblPassword.setBounds(40, 135, 100, 25);
        add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(40, 160, 260, 30);
        add(txtPassword);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(40, 215, 120, 35);
        add(btnLogin);

        btnBack = new JButton("Back");
        btnBack.setBounds(180, 215, 120, 35);
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
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM users WHERE username = ? AND password = ? AND role = 'admin'");
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                con.close();
                new AdminDashboard().setVisible(true);
                dispose();
            } else {
                con.close();
                JOptionPane.showMessageDialog(this, "Invalid admin username or password.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}