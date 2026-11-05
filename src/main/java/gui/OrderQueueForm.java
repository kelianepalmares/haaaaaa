package gui;

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
import model.Order;
import service.OrderDAO;
import service.RouteService;
import structures.OrderPriorityQueue;
import structures.RouteGraph;

public class OrderQueueForm extends JFrame {

    private JLabel lblQueue;
    private JLabel lblOut;
    private JButton btnRefresh;
    private JButton btnDispatch;
    private JButton btnDelivered;
    private JTable tableQueue;
    private JTable tableOut;
    private DefaultTableModel queueModel;
    private DefaultTableModel outModel;
    private JScrollPane scrollQueue;
    private JScrollPane scrollOut;

    private OrderDAO dao = new OrderDAO();
    private OrderPriorityQueue queue = new OrderPriorityQueue();

    public OrderQueueForm() {
        setTitle("Order Queue");
        setSize(850, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        lblQueue = new JLabel("Preparing (priority queue: most urgent and shortest delivery time first)");
        lblQueue.setBounds(20, 15, 600, 25);
        add(lblQueue);

        String[] queueColumns = {"Rank", "Order ID", "Customer", "Restaurant", "Urgency", "Delivery Time (min)", "Status"};
        queueModel = new DefaultTableModel(queueColumns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableQueue = new JTable(queueModel);
        scrollQueue = new JScrollPane(tableQueue);
        scrollQueue.setBounds(20, 45, 795, 200);
        add(scrollQueue);

        btnDispatch = new JButton("Dispatch Next Order");
        btnDispatch.setBounds(20, 255, 180, 30);
        add(btnDispatch);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(210, 255, 120, 30);
        add(btnRefresh);

        lblOut = new JLabel("Out for Delivery");
        lblOut.setBounds(20, 305, 300, 25);
        add(lblOut);

        outModel = new DefaultTableModel(queueColumns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableOut = new JTable(outModel);
        scrollOut = new JScrollPane(tableOut);
        scrollOut.setBounds(20, 335, 795, 150);
        add(scrollOut);

        btnDelivered = new JButton("Mark as Delivered");
        btnDelivered.setBounds(20, 495, 180, 30);
        add(btnDelivered);

        btnDispatch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispatchNext();
            }
        });

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        btnDelivered.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                markDelivered();
            }
        });

        loadData();
    }

    private void loadData() {
        try {
            queue = new OrderPriorityQueue();
            ArrayList<Order> preparing = dao.getByStatus("Preparing");
            for (int i = 0; i < preparing.size(); i++) {
                queue.insert(preparing.get(i));
            }

            queueModel.setRowCount(0);
            ArrayList<Order> sorted = queue.toSortedList();
            for (int i = 0; i < sorted.size(); i++) {
                addRow(queueModel, i + 1, sorted.get(i));
            }

            outModel.setRowCount(0);
            ArrayList<Order> out = dao.getByStatus("Out for Delivery");
            for (int i = 0; i < out.size(); i++) {
                addRow(outModel, i + 1, out.get(i));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void addRow(DefaultTableModel model, int rank, Order o) throws Exception {
        model.addRow(new Object[]{
            rank,
            o.getOrderId(),
            dao.getCustomerName(o.getCustomerId()),
            dao.getRestaurantName(o.getRestaurantId()),
            OrderDAO.urgencyText(o.getUrgency()),
            o.getDeliveryTime(),
            o.getStatus()
        });
    }

    private void dispatchNext() {
        if (queue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No orders waiting in the queue.");
            return;
        }

        try {
            Order next = queue.extractMin();
            dao.updateStatus(next.getOrderId(), "Out for Delivery");

            String from = dao.getRestaurantLocation(next.getRestaurantId());
            String to = dao.getCustomerAddress(next.getCustomerId());
            String routeText;

            if (from.equalsIgnoreCase(to)) {
                routeText = "Same location, no travel needed.";
            } else {
                RouteGraph graph = RouteService.buildGraph();
                ArrayList<String> path = graph.shortestPath(from, to);
                if (path.isEmpty()) {
                    routeText = "No route found from " + from + " to " + to + ".";
                } else {
                    routeText = RouteService.pathToString(path) + " (" + graph.getLastDistance() + ")";
                }
            }

            JOptionPane.showMessageDialog(this, "Order #" + next.getOrderId()
                    + " is now Out for Delivery.\nRoute: " + routeText);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void markDelivered() {
        int row = tableOut.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an order in the Out for Delivery table first.");
            return;
        }

        try {
            int orderId = Integer.parseInt(outModel.getValueAt(row, 1).toString());
            dao.updateStatus(orderId, "Delivered");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}