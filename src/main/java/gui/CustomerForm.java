package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.Customer;
import service.CustomerDAO;
import service.OrderDAO;

public class CustomerForm extends JFrame {

    private JLabel lblName;
    private JLabel lblAddress;
    private JLabel lblUsername;
    private JLabel lblPassword;
    private JLabel lblSearch;
    private JTextField txtName;
    private JTextField txtAddress;
    private JTextField txtUsername;
    private JTextField txtPassword;
    private JTextField txtSearch;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnHistory;
    private JButton btnSearch;
    private JButton btnShowAll;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private CustomerDAO dao = new CustomerDAO();
    private OrderDAO orderDAO = new OrderDAO();
    private int selectedId = -1;

    public CustomerForm() {
        setTitle("Customer Management");
        setSize(850, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        lblName = new JLabel("Name:");
        lblName.setBounds(30, 20, 80, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(110, 20, 200, 25);
        add(txtName);

        lblAddress = new JLabel("Address:");
        lblAddress.setBounds(30, 55, 80, 25);
        add(lblAddress);

        txtAddress = new JTextField();
        txtAddress.setBounds(110, 55, 200, 25);
        add(txtAddress);

        lblUsername = new JLabel("Username:");
        lblUsername.setBounds(30, 90, 80, 25);
        add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(110, 90, 200, 25);
        add(txtUsername);

        lblPassword = new JLabel("Password:");
        lblPassword.setBounds(30, 125, 80, 25);
        add(lblPassword);

        txtPassword = new JTextField();
        txtPassword.setBounds(110, 125, 200, 25);
        add(txtPassword);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(350, 20, 120, 25);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(480, 20, 120, 25);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(610, 20, 120, 25);
        add(btnDelete);

        btnClear = new JButton("Clear");
        btnClear.setBounds(350, 55, 120, 25);
        add(btnClear);

        btnHistory = new JButton("Order History");
        btnHistory.setBounds(480, 55, 120, 25);
        add(btnHistory);

        lblSearch = new JLabel("Search Name:");
        lblSearch.setBounds(30, 175, 90, 25);
        add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(120, 175, 190, 25);
        add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(350, 175, 120, 25);
        add(btnSearch);

        btnShowAll = new JButton("Show All");
        btnShowAll.setBounds(480, 175, 120, 25);
        add(btnShowAll);

        String[] columns = {"ID", "Name", "Address", "Username"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 215, 770, 320);
        add(scrollPane);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCustomer();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateCustomer();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteCustomer();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        btnHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHistory();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchCustomer();
            }
        });

        btnShowAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                txtSearch.setText("");
                loadData();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtAddress.setText(tableModel.getValueAt(row, 2).toString());
                    txtUsername.setText(tableModel.getValueAt(row, 3).toString());
                    txtPassword.setText("");
                }
            }
        });

        loadData();
    }

    private void showTable(ArrayList<Customer> list) {
        tableModel.setRowCount(0);
        for (int i = 0; i < list.size(); i++) {
            Customer c = list.get(i);
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getAddress(), c.getUsername()});
        }
    }

    private void loadData() {
        try {
            showTable(dao.getAll());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void addCustomer() {
        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (name.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            if (dao.add(name, address, username, password)) {
                clearFields();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Duplicate entry: username already exists.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void updateCustomer() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer from the table first.");
            return;
        }

        String name = txtName.getText().trim();
        String address = txtAddress.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (name.isEmpty() || address.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields (enter the password again).");
            return;
        }

        try {
            if (dao.update(selectedId, name, address, username, password)) {
                clearFields();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Duplicate entry: username already exists.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete this customer and all of their orders?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.delete(selectedId);
            clearFields();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    

    private void searchCustomer() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a name to search.");
            return;
        }

        try {
            ArrayList<Customer> result = dao.searchByName(keyword);
            showTable(result);
            if (result.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No customers found.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void showHistory() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a customer from the table first.");
            return;
        }

        try {
            ArrayList<String[]> rows = orderDAO.getByCustomer(selectedId);
            if (rows.isEmpty()) {
                JOptionPane.showMessageDialog(this, "This customer has no orders yet.");
                return;
            }
            String text = "Order history of " + txtName.getText() + ":\n\n";
            for (int i = 0; i < rows.size(); i++) {
                String[] r = rows.get(i);
                text += "Order #" + r[0] + " | " + r[1] + " | " + r[2]
                        + " | " + r[3] + " min | P" + r[4] + "\n";
            }
            JOptionPane.showMessageDialog(this, text);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtAddress.setText("");
        txtUsername.setText("");
        txtPassword.setText("");
        selectedId = -1;
        table.clearSelection();
    }
}