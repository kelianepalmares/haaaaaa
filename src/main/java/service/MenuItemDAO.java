package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import model.MenuItem;

public class MenuItemDAO {

    public ArrayList<MenuItem> getByRestaurant(int restaurantId) throws SQLException {
        ArrayList<MenuItem> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM menu_items WHERE restaurant_id = ? ORDER BY name");
        ps.setInt(1, restaurantId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new MenuItem(
                    rs.getInt("id"),
                    rs.getInt("restaurant_id"),
                    rs.getString("name"),
                    rs.getDouble("price")));
        }
        con.close();
        return list;
    }

    // False kung may kaparehong pangalan na sa restaurant
    public boolean add(int restaurantId, String name, double price) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement check = con.prepareStatement(
                "SELECT id FROM menu_items WHERE restaurant_id = ? AND name = ?");
        check.setInt(1, restaurantId);
        check.setString(2, name);
        ResultSet rs = check.executeQuery();
        if (rs.next()) {
            con.close();
            return false;
        }
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO menu_items (restaurant_id, name, price) VALUES (?, ?, ?)");
        ps.setInt(1, restaurantId);
        ps.setString(2, name);
        ps.setDouble(3, price);
        ps.executeUpdate();
        con.close();
        return true;
    }

    public void update(int id, String name, double price) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "UPDATE menu_items SET name = ?, price = ? WHERE id = ?");
        ps.setString(1, name);
        ps.setDouble(2, price);
        ps.setInt(3, id);
        ps.executeUpdate();
        con.close();
    }

    public void delete(int id) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM menu_items WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
        con.close();
    }
}