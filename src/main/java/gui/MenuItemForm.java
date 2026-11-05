package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.MenuItem;
import model.Restaurant;
import service.MenuItemDAO;
import service.RestaurantDAO;

public class MenuItemForm extends JFrame {

    private JLabel lblRestaurant;
    private JLabel lblName;
    private JLabel lblPrice;
    private JComboBox<Restaurant> cmbRestaurant;
    private JTextField txtName;
    private JTextField txtPrice;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private MenuItemDAO dao = new MenuItemDAO();
    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private int selectedId = -1;

    public MenuItemForm() {
        setTitle("Menu Item Management");
        setSize(800, 550);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        lblRestaurant = new JLabel("Restaurant:");
        lblRestaurant.setBounds(30, 20, 80, 25);
        add(lblRestaurant);

        cmbRestaurant = new JComboBox<>();
        cmbRestaurant.setBounds(110, 20, 260, 25);
        add(cmbRestaurant);

        lblName = new JLabel("Item Name:");
        lblName.setBounds(30, 60, 80, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(110, 60, 200, 25);
        add(txtName);

        lblPrice = new JLabel("Price:");
        lblPrice.setBounds(30, 95, 80, 25);
        add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(110, 95, 100, 25);
        add(txtPrice);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(340, 60, 110, 25);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(460, 60, 110, 25);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(580, 60, 110, 25);
        add(btnDelete);

        btnClear = new JButton("Clear");
        btnClear.setBounds(340, 95, 110, 25);
        add(btnClear);

        String[] columns = {"ID", "Item Name", "Price"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 140, 720, 350);
        add(scrollPane);

        cmbRestaurant.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
                loadItems();
            }
        });

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateItem();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteItem();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtPrice.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });

        loadRestaurants();
    }

    private void loadRestaurants() {
        try {
            ArrayList<Restaurant> list = restaurantDAO.getAll();
            for (int i = 0; i < list.size(); i++) {
                cmbRestaurant.addItem(list.get(i));
            }
            if (list.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No restaurants yet. Add a restaurant first.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void loadItems() {
        Restaurant r = (Restaurant) cmbRestaurant.getSelectedItem();
        tableModel.setRowCount(0);
        if (r == null) {
            return;
        }

        try {
            ArrayList<MenuItem> items = dao.getByRestaurant(r.getId());
            for (int i = 0; i < items.size(); i++) {
                MenuItem m = items.get(i);
                tableModel.addRow(new Object[]{m.getId(), m.getName(), m.getPrice()});
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void addItem() {
        Restaurant r = (Restaurant) cmbRestaurant.getSelectedItem();
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Select a restaurant first.");
            return;
        }

        String name = txtName.getText().trim();
        if (name.isEmpty() || txtPrice.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(txtPrice.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number.");
            return;
        }
        if (price <= 0) {
            JOptionPane.showMessageDialog(this, "Price must be greater than 0.");
            return;
        }

        try {
            if (dao.add(r.getId(), name, price)) {
                clearFields();
                loadItems();
            } else {
                JOptionPane.showMessageDialog(this, "Duplicate entry: item already exists in this restaurant.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void updateItem() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select an item from the table first.");
            return;
        }

        String name = txtName.getText().trim();
        if (name.isEmpty() || txtPrice.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(txtPrice.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Price must be a number.");
            return;
        }
        if (price <= 0) {
            JOptionPane.showMessageDialog(this, "Price must be greater than 0.");
            return;
        }

        try {
            dao.update(selectedId, name, price);
            clearFields();
            loadItems();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void deleteItem() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select an item from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this item?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.delete(selectedId);
            clearFields();
            loadItems();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtPrice.setText("");
        selectedId = -1;
        table.clearSelection();
    }
}