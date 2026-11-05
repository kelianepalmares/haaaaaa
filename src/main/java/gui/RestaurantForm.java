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
import model.Restaurant;
import service.RestaurantDAO;
import structures.RestaurantHashTable;

public class RestaurantForm extends JFrame {

    private JLabel lblName;
    private JLabel lblLocation;
    private JLabel lblSearch;
    private JTextField txtName;
    private JTextField txtLocation;
    private JTextField txtSearch;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSearch;
    private JButton btnShowAll;
    private JButton btnClear;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollPane;

    private RestaurantDAO dao = new RestaurantDAO();
    private RestaurantHashTable hashTable = new RestaurantHashTable(16);
    private int selectedId = -1;

    public RestaurantForm() {
        setTitle("Restaurant Management");
        setSize(800, 550);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        lblName = new JLabel("Name:");
        lblName.setBounds(30, 20, 80, 25);
        add(lblName);

        txtName = new JTextField();
        txtName.setBounds(110, 20, 200, 25);
        add(txtName);

        lblLocation = new JLabel("Location:");
        lblLocation.setBounds(30, 55, 80, 25);
        add(lblLocation);

        txtLocation = new JTextField();
        txtLocation.setBounds(110, 55, 200, 25);
        add(txtLocation);

        btnAdd = new JButton("Add");
        btnAdd.setBounds(340, 20, 110, 25);
        add(btnAdd);

        btnUpdate = new JButton("Update");
        btnUpdate.setBounds(460, 20, 110, 25);
        add(btnUpdate);

        btnDelete = new JButton("Delete");
        btnDelete.setBounds(580, 20, 110, 25);
        add(btnDelete);

        btnClear = new JButton("Clear");
        btnClear.setBounds(340, 55, 110, 25);
        add(btnClear);

        lblSearch = new JLabel("Search Location:");
        lblSearch.setBounds(30, 100, 110, 25);
        add(lblSearch);

        txtSearch = new JTextField();
        txtSearch.setBounds(140, 100, 170, 25);
        add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBounds(340, 100, 110, 25);
        add(btnSearch);

        btnShowAll = new JButton("Show All");
        btnShowAll.setBounds(460, 100, 110, 25);
        add(btnShowAll);

        String[] columns = {"ID", "Name", "Location"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        scrollPane = new JScrollPane(table);
        scrollPane.setBounds(30, 145, 720, 340);
        add(scrollPane);

        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRestaurant();
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateRestaurant();
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRestaurant();
            }
        });

        btnClear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clearFields();
            }
        });

        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                searchByLocation();
            }
        });

        btnShowAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showTable(hashTable.getAll());
            }
        });

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    selectedId = (int) tableModel.getValueAt(row, 0);
                    txtName.setText(tableModel.getValueAt(row, 1).toString());
                    txtLocation.setText(tableModel.getValueAt(row, 2).toString());
                }
            }
        });

        loadData();
    }

    // Kunin sa database, ilagay sa hash table, ipakita sa table
    private void loadData() {
        try {
            hashTable.clear();
            ArrayList<Restaurant> list = dao.getAll();
            for (int i = 0; i < list.size(); i++) {
                hashTable.insert(list.get(i));
            }
            showTable(hashTable.getAll());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void showTable(ArrayList<Restaurant> list) {
        tableModel.setRowCount(0);
        for (int i = 0; i < list.size(); i++) {
            Restaurant r = list.get(i);
            tableModel.addRow(new Object[]{r.getId(), r.getName(), r.getLocation()});
        }
    }

    private void addRestaurant() {
        String name = txtName.getText().trim();
        String location = txtLocation.getText().trim();

        if (name.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            if (dao.add(name, location)) {
                JOptionPane.showMessageDialog(this, "Restaurant added.");
                clearFields();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Duplicate entry: restaurant name already exists.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void updateRestaurant() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a restaurant from the table first.");
            return;
        }

        String name = txtName.getText().trim();
        String location = txtLocation.getText().trim();

        if (name.isEmpty() || location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            if (dao.update(selectedId, name, location)) {
                JOptionPane.showMessageDialog(this, "Restaurant updated.");
                clearFields();
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Duplicate entry: restaurant name already exists.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void deleteRestaurant() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Select a restaurant from the table first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this restaurant?",
                "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            dao.delete(selectedId);
            JOptionPane.showMessageDialog(this, "Restaurant deleted.");
            clearFields();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    // Gumagamit ng hash table para sa mabilis na search
    private void searchByLocation() {
        String location = txtSearch.getText().trim();
        if (location.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a location to search.");
            return;
        }

        ArrayList<Restaurant> result = hashTable.getByLocation(location);
        showTable(result);

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No restaurants found in " + location + ".");
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtLocation.setText("");
        txtSearch.setText("");
        selectedId = -1;
        table.clearSelection();
    }
}