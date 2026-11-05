package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import service.LocationDAO;
import service.RouteService;
import structures.RouteGraph;

public class LocationForm extends JFrame {

    private JLabel lblLocation;
    private JLabel lblFrom;
    private JLabel lblTo;
    private JLabel lblDistance;
    private JLabel lblResult;
    private JTextField txtLocation;
    private JTextField txtDistance;
    private JComboBox<String> cmbFrom;
    private JComboBox<String> cmbTo;
    private JButton btnAddLocation;
    private JButton btnDeleteLocation;
    private JButton btnAddRoute;
    private JButton btnDeleteRoute;
    private JButton btnFind;
    private DefaultListModel<String> locationModel = new DefaultListModel<>();
    private JList<String> listLocations;
    private JScrollPane scrollLocations;
    private JTable table;
    private DefaultTableModel tableModel;
    private JScrollPane scrollTable;

    private LocationDAO dao = new LocationDAO();

    public LocationForm() {
        setTitle("Locations and Routes");
        setSize(800, 580);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        lblLocation = new JLabel("Location:");
        lblLocation.setBounds(20, 20, 70, 25);
        add(lblLocation);

        txtLocation = new JTextField();
        txtLocation.setBounds(95, 20, 170, 25);
        add(txtLocation);

        btnAddLocation = new JButton("Add");
        btnAddLocation.setBounds(275, 20, 80, 25);
        add(btnAddLocation);

        btnDeleteLocation = new JButton("Delete");
        btnDeleteLocation.setBounds(360, 20, 80, 25);
        add(btnDeleteLocation);

        listLocations = new JList<>(locationModel);
        scrollLocations = new JScrollPane(listLocations);
        scrollLocations.setBounds(20, 55, 420, 100);
        add(scrollLocations);

        lblFrom = new JLabel("From:");
        lblFrom.setBounds(20, 175, 40, 25);
        add(lblFrom);

        cmbFrom = new JComboBox<>();
        cmbFrom.setBounds(65, 175, 150, 25);
        add(cmbFrom);

        lblTo = new JLabel("To:");
        lblTo.setBounds(230, 175, 30, 25);
        add(lblTo);

        cmbTo = new JComboBox<>();
        cmbTo.setBounds(260, 175, 150, 25);
        add(cmbTo);

        lblDistance = new JLabel("Distance:");
        lblDistance.setBounds(425, 175, 60, 25);
        add(lblDistance);

        txtDistance = new JTextField();
        txtDistance.setBounds(490, 175, 60, 25);
        add(txtDistance);

        btnAddRoute = new JButton("Add Route");
        btnAddRoute.setBounds(560, 175, 100, 25);
        add(btnAddRoute);

        btnDeleteRoute = new JButton("Delete Route");
        btnDeleteRoute.setBounds(665, 175, 100, 25);
        add(btnDeleteRoute);

        btnFind = new JButton("Find Shortest Route");
        btnFind.setBounds(20, 215, 170, 28);
        add(btnFind);

        lblResult = new JLabel("Choose From and To, then click Find Shortest Route.");
        lblResult.setBounds(200, 215, 570, 28);
        add(lblResult);

        String[] columns = {"ID", "From", "To", "Distance"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        scrollTable = new JScrollPane(table);
        scrollTable.setBounds(20, 260, 745, 260);
        add(scrollTable);

        btnAddLocation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addLocation();
            }
        });

        btnDeleteLocation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteLocation();
            }
        });

        btnAddRoute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addRoute();
            }
        });

        btnDeleteRoute.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRoute();
            }
        });

        btnFind.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                findRoute();
            }
        });

        loadData();
    }

    private void loadData() {
        try {
            locationModel.clear();
            cmbFrom.removeAllItems();
            cmbTo.removeAllItems();
            ArrayList<String> locations = dao.getAllLocations();
            for (int i = 0; i < locations.size(); i++) {
                locationModel.addElement(locations.get(i));
                cmbFrom.addItem(locations.get(i));
                cmbTo.addItem(locations.get(i));
            }

            tableModel.setRowCount(0);
            ArrayList<String[]> routes = dao.getAllRoutes();
            for (int i = 0; i < routes.size(); i++) {
                tableModel.addRow(routes.get(i));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void addLocation() {
        String name = txtLocation.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a location name.");
            return;
        }

        try {
            if (dao.addLocation(name)) {
                txtLocation.setText("");
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Duplicate entry: location already exists.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void deleteLocation() {
        String selected = listLocations.getSelectedValue();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Select a location first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete " + selected + " and its routes?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            ArrayList<String[]> routes = dao.getAllRoutes();
            for (int i = 0; i < routes.size(); i++) {
                String[] r = routes.get(i);
                if (r[1].equals(selected) || r[2].equals(selected)) {
                    dao.deleteRoute(Integer.parseInt(r[0]));
                }
            }
            dao.deleteLocation(selected);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void addRoute() {
        String from = (String) cmbFrom.getSelectedItem();
        String to = (String) cmbTo.getSelectedItem();

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this, "Add locations first.");
            return;
        }
        if (from.equals(to)) {
            JOptionPane.showMessageDialog(this, "Choose two different locations.");
            return;
        }

        double distance;
        try {
            distance = Double.parseDouble(txtDistance.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Distance must be a number.");
            return;
        }
        if (distance <= 0) {
            JOptionPane.showMessageDialog(this, "Distance must be greater than 0.");
            return;
        }

        try {
            dao.addRoute(from, to, distance);
            txtDistance.setText("");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void deleteRoute() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a route in the table first.");
            return;
        }

        try {
            int id = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
            dao.deleteRoute(id);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
    
    private void findRoute() {
        String from = (String) cmbFrom.getSelectedItem();
        String to = (String) cmbTo.getSelectedItem();

        if (from == null || to == null) {
            JOptionPane.showMessageDialog(this, "Add locations first.");
            return;
        }

        try {
            RouteGraph graph = RouteService.buildGraph();
            ArrayList<String> path = graph.shortestPath(from, to);
            if (path.isEmpty()) {
                lblResult.setText("No route found between " + from + " and " + to + ".");
            } else {
                lblResult.setText("Shortest: " + RouteService.pathToString(path)
                        + "   |   Distance: " + graph.getLastDistance());
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}