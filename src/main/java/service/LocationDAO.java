package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;

public class LocationDAO {

    // Kunin lahat ng location names
    public ArrayList<String> getAllLocations() throws SQLException {
        ArrayList<String> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT name FROM locations ORDER BY name");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(rs.getString("name"));
        }
        con.close();
        return list;
    }

    // Add location. False kung duplicate.
    public boolean addLocation(String name) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement("INSERT INTO locations (name) VALUES (?)");
            ps.setString(1, name);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } finally {
            con.close();
        }
    }

    // Delete location
    public void deleteLocation(String name) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM locations WHERE name = ?");
        ps.setString(1, name);
        ps.executeUpdate();
        con.close();
    }

    // Add route (distance) sa pagitan ng dalawang location
    public void addRoute(String from, String to, double distance) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "INSERT INTO routes (from_location, to_location, distance) VALUES (?, ?, ?)");
        ps.setString(1, from);
        ps.setString(2, to);
        ps.setDouble(3, distance);
        ps.executeUpdate();
        con.close();
    }

    // Kunin lahat ng routes: bawat row ay {from, to, distance}
    public ArrayList<String[]> getAllRoutes() throws SQLException {
        ArrayList<String[]> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM routes ORDER BY id");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(new String[]{
                rs.getString("id"),
                rs.getString("from_location"),
                rs.getString("to_location"),
                String.valueOf(rs.getDouble("distance"))
            });
        }
        con.close();
        return list;
    }

    // Delete route gamit ang id
    public void deleteRoute(int id) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM routes WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
        con.close();
    }
}