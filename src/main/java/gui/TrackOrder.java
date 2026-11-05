package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import service.OrderDAO;

public class TrackOrder extends JFrame {

    private Customer customer;
    private JLabel lblTitle;
    private JButton btnRefresh;
    private JButton btnBack;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private OrderDAO dao = new OrderDAO();

    public TrackOrder(Customer customer) {
        this.customer = customer;
        setTitle("Track Order");
        setSize(650, 450);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        lblTitle = new JLabel("Track My Orders");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 18));
        lblTitle.setBounds(20, 15, 300, 30);
        add(lblTitle);

        String[] columns = {"Order ID", "Restaurant", "Status", "Delivery Time (min)", "Total"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 60, 600, 280);
        add(scrollPane);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(20, 355, 120, 30);
        add(btnRefresh);

        btnBack = new JButton("Back");
        btnBack.setBounds(500, 355, 120, 30);
        add(btnBack);

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadOrders();
            }
        });

        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new HomePage(TrackOrder.this.customer).setVisible(true);
                dispose();
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        try {
            tableModel.setRowCount(0);
            ArrayList<String[]> rows = dao.getByCustomer(customer.getId());
            for (int i = 0; i < rows.size(); i++) {
                tableModel.addRow(rows.get(i));
            }
            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "You have no orders yet.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}