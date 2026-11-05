package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import model.Restaurant;

public class RestaurantDAO {

    // Kunin lahat ng restaurants mula sa database
    public ArrayList<Restaurant> getAll() throws SQLException {
        ArrayList<Restaurant> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM restaurants ORDER BY id");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new Restaurant(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("location")));
        }
        con.close();
        return list;
    }

    // Add restaurant. False kung duplicate ang name.
    public boolean add(String name, String location) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO restaurants (name, location) VALUES (?, ?)");
            ps.setString(1, name);
            ps.setString(2, location);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } finally {
            con.close();
        }
    }

    // Update restaurant. False kung duplicate ang name.
    public boolean update(int id, String name, String location) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE restaurants SET name = ?, location = ? WHERE id = ?");
            ps.setString(1, name);
            ps.setString(2, location);
            ps.setInt(3, id);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } finally {
            con.close();
        }
    }

    // Delete restaurant
    public void delete(int id) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM restaurants WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
        con.close();
    }
}