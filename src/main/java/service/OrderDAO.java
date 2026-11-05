package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import model.Order;
import model.OrderItem;

public class OrderDAO {

    public static String urgencyText(int urgency) {
        if (urgency == 1) {
            return "Urgent";
        } else if (urgency == 2) {
            return "High";
        } else if (urgency == 3) {
            return "Normal";
        }
        return "Low";
    }

    // Nagre-return ng bagong order ID
    public int placeOrder(int customerId, int restaurantId, int urgency, int deliveryTime,
            ArrayList<OrderItem> items) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO orders (customer_id, restaurant_id, status, urgency, delivery_time) "
                    + "VALUES (?, ?, 'Preparing', ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customerId);
            ps.setInt(2, restaurantId);
            ps.setInt(3, urgency);
            ps.setInt(4, deliveryTime);
            ps.executeUpdate();

            ResultSet keys = ps.getGeneratedKeys();
            int orderId = 0;
            if (keys.next()) {
                orderId = keys.getInt(1);
            }

            PreparedStatement ps2 = con.prepareStatement(
                    "INSERT INTO order_items (order_id, menu_item_id, quantity) VALUES (?, ?, ?)");
            for (int i = 0; i < items.size(); i++) {
                ps2.setInt(1, orderId);
                ps2.setInt(2, items.get(i).getMenuItem().getId());
                ps2.setInt(3, items.get(i).getQuantity());
                ps2.executeUpdate();
            }

            con.commit();
            return orderId;
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            con.close();
        }
    }

    public ArrayList<Order> getByStatus(String status) throws SQLException {
        ArrayList<Order> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM orders WHERE status = ? ORDER BY order_id");
        ps.setString(1, status);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Order(
                    rs.getInt("order_id"),
                    rs.getInt("customer_id"),
                    rs.getInt("restaurant_id"),
                    rs.getString("status"),
                    rs.getInt("urgency"),
                    rs.getInt("delivery_time")));
        }
        con.close();
        return list;
    }

    public void updateStatus(int orderId, String status) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("UPDATE orders SET status = ? WHERE order_id = ?");
        ps.setString(1, status);
        ps.setInt(2, orderId);
        ps.executeUpdate();
        con.close();
    }

    // Rows: {order id, restaurant, status, delivery time, total}
    public ArrayList<String[]> getByCustomer(int customerId) throws SQLException {
        ArrayList<String[]> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT o.order_id, r.name, o.status, o.delivery_time, "
                + "COALESCE(SUM(m.price * oi.quantity), 0) AS total "
                + "FROM orders o "
                + "JOIN restaurants r ON r.id = o.restaurant_id "
                + "LEFT JOIN order_items oi ON oi.order_id = o.order_id "
                + "LEFT JOIN menu_items m ON m.id = oi.menu_item_id "
                + "WHERE o.customer_id = ? "
                + "GROUP BY o.order_id, r.name, o.status, o.delivery_time "
                + "ORDER BY o.order_id DESC");
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            rows.add(new String[]{
                rs.getString(1), rs.getString(2), rs.getString(3),
                rs.getString(4), String.format("%.2f", rs.getDouble(5))
            });
        }
        con.close();
        return rows;
    }

    // Rows: {order id, customer, restaurant, urgency, status, delivery time, date, total}
    public ArrayList<String[]> getAllHistory() throws SQLException {
        ArrayList<String[]> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT o.order_id, c.name, r.name, o.urgency, o.status, o.delivery_time, o.order_date, "
                + "COALESCE(SUM(m.price * oi.quantity), 0) AS total "
                + "FROM orders o "
                + "JOIN customers c ON c.id = o.customer_id "
                + "JOIN restaurants r ON r.id = o.restaurant_id "
                + "LEFT JOIN order_items oi ON oi.order_id = o.order_id "
                + "LEFT JOIN menu_items m ON m.id = oi.menu_item_id "
                + "GROUP BY o.order_id, c.name, r.name, o.urgency, o.status, o.delivery_time, o.order_date "
                + "ORDER BY o.order_id");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            rows.add(new String[]{
                rs.getString(1), rs.getString(2), rs.getString(3),
                urgencyText(rs.getInt(4)), rs.getString(5), rs.getString(6),
                rs.getString(7), String.format("%.2f", rs.getDouble(8))
            });
        }
        con.close();
        return rows;
    }

    // Rows: {restaurant, number of orders, average, shortest, longest}
    public ArrayList<String[]> getDeliveryTimeReport() throws SQLException {
        ArrayList<String[]> rows = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT r.name, COUNT(*), AVG(o.delivery_time), MIN(o.delivery_time), MAX(o.delivery_time) "
                + "FROM orders o JOIN restaurants r ON r.id = o.restaurant_id "
                + "GROUP BY r.name ORDER BY r.name");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            rows.add(new String[]{
                rs.getString(1), rs.getString(2),
                String.format("%.1f", rs.getDouble(3)),
                rs.getString(4), rs.getString(5)
            });
        }
        con.close();
        return rows;
    }

    public double getOverallAverage() throws SQLException {
        double avg = 0;
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT AVG(delivery_time) FROM orders");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            avg = rs.getDouble(1);
        }
        con.close();
        return avg;
    }

    private String getValue(String sql, int id) throws SQLException {
        String value = "";
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, id);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            value = rs.getString(1);
        }
        con.close();
        return value;
    }

    public String getCustomerName(int id) throws SQLException {
        return getValue("SELECT name FROM customers WHERE id = ?", id);
    }

    public String getCustomerAddress(int id) throws SQLException {
        return getValue("SELECT address FROM customers WHERE id = ?", id);
    }

    public String getRestaurantName(int id) throws SQLException {
        return getValue("SELECT name FROM restaurants WHERE id = ?", id);
    }

    public String getRestaurantLocation(int id) throws SQLException {
        return getValue("SELECT location FROM restaurants WHERE id = ?", id);
    }
}