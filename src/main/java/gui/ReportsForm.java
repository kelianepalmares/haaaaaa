package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import service.OrderDAO;

public class ReportsForm extends JFrame {

    private JTabbedPane tabs;
    private JTable historyTable;
    private JTable deliveryTable;
    private DefaultTableModel historyModel;
    private DefaultTableModel deliveryModel;
    private JLabel lblSummary;
    private JButton btnRefresh;

    private OrderDAO dao = new OrderDAO();

    public ReportsForm() {
        setTitle("Reports");
        setSize(900, 560);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        String[] historyColumns = {"Order ID", "Customer", "Restaurant", "Urgency", "Status",
            "Delivery Time (min)", "Date", "Total"};
        historyModel = new DefaultTableModel(historyColumns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        historyTable = new JTable(historyModel);

        String[] deliveryColumns = {"Restaurant", "Orders", "Average (min)", "Shortest (min)", "Longest (min)"};
        deliveryModel = new DefaultTableModel(deliveryColumns, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        deliveryTable = new JTable(deliveryModel);

        tabs = new JTabbedPane();
        tabs.setBounds(20, 20, 840, 420);
        tabs.addTab("Order History", new JScrollPane(historyTable));
        tabs.addTab("Delivery Times", new JScrollPane(deliveryTable));
        add(tabs);

        lblSummary = new JLabel("");
        lblSummary.setBounds(20, 455, 700, 30);
        add(lblSummary);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setBounds(740, 455, 120, 30);
        add(btnRefresh);

        btnRefresh.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadData();
            }
        });

        loadData();
    }

    private void loadData() {
        try {
            historyModel.setRowCount(0);
            ArrayList<String[]> history = dao.getAllHistory();
            double sales = 0;
            for (int i = 0; i < history.size(); i++) {
                historyModel.addRow(history.get(i));
                sales += Double.parseDouble(history.get(i)[7]);
            }

            deliveryModel.setRowCount(0);
            ArrayList<String[]> delivery = dao.getDeliveryTimeReport();
            for (int i = 0; i < delivery.size(); i++) {
                deliveryModel.addRow(delivery.get(i));
            }

            lblSummary.setText("Total orders: " + history.size()
                    + "   |   Overall average delivery time: "
                    + String.format("%.1f", dao.getOverallAverage()) + " min"
                    + "   |   Total sales: P" + String.format("%.2f", sales));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }
}