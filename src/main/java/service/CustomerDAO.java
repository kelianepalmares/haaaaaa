package service;

import database.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import model.Customer;

public class CustomerDAO {

    private Customer readCustomer(ResultSet rs) throws SQLException {
        return new Customer(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("address"),
                rs.getString("username"),
                rs.getString("password"));
    }

    // Null kung mali ang username o password
    public Customer login(String username, String password) throws SQLException {
        Customer c = null;
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM customers WHERE username = ? AND password = ?");
        ps.setString(1, username);
        ps.setString(2, password);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            c = readCustomer(rs);
        }
        con.close();
        return c;
    }

    public ArrayList<Customer> getAll() throws SQLException {
        ArrayList<Customer> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("SELECT * FROM customers ORDER BY id");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(readCustomer(rs));
        }
        con.close();
        return list;
    }

    public ArrayList<Customer> searchByName(String keyword) throws SQLException {
        ArrayList<Customer> list = new ArrayList<>();
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement(
                "SELECT * FROM customers WHERE name LIKE ? ORDER BY id");
        ps.setString(1, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            list.add(readCustomer(rs));
        }
        con.close();
        return list;
    }

    // False kung duplicate ang username
    public boolean add(String name, String address, String username, String password) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO customers (name, address, username, password) VALUES (?, ?, ?, ?)");
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } finally {
            con.close();
        }
    }

    // False kung duplicate ang username
    public boolean update(int id, String name, String address, String username, String password) throws SQLException {
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE customers SET name = ?, address = ?, username = ?, password = ? WHERE id = ?");
            ps.setString(1, name);
            ps.setString(2, address);
            ps.setString(3, username);
            ps.setString(4, password);
            ps.setInt(5, id);
            ps.executeUpdate();
            return true;
        } catch (SQLIntegrityConstraintViolationException e) {
            return false;
        } finally {
            con.close();
        }
    }

    public void delete(int id) throws SQLException {
        Connection con = DBConnection.getConnection();
        PreparedStatement ps = con.prepareStatement("DELETE FROM customers WHERE id = ?");
        ps.setInt(1, id);
        ps.executeUpdate();
        con.close();
    }
}