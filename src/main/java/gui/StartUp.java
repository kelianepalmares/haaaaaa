package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class StartUp extends JFrame {

    private JLabel lblWelcome;
    private JButton btnLogin;
    private JButton btnCreateAccount;
    private JButton btnAdminLogin;

    public StartUp() {
        setTitle("Online Food Delivery");
        setSize(350, 380);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblWelcome = new JLabel("Welcome to Online Food Delivery!");
        lblWelcome.setBounds(60, 30, 250, 30);
        add(lblWelcome);

        btnLogin = new JButton("Login");
        btnLogin.setBounds(70, 90, 200, 50);
        add(btnLogin);

        btnCreateAccount = new JButton("Create an Account");
        btnCreateAccount.setBounds(70, 160, 200, 50);
        add(btnCreateAccount);

        btnAdminLogin = new JButton("Admin Login");
        btnAdminLogin.setBounds(70, 230, 200, 50);
        add(btnAdminLogin);

        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UserLogin().setVisible(true);
                dispose();
            }
        });

        btnCreateAccount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new UserRegistration().setVisible(true);
                dispose();
            }
        });

        btnAdminLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminLogin().setVisible(true);
                dispose();
            }
        });
    }
}