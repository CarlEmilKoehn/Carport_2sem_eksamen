package app.persistence;

import app.entities.Customer;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerMapper {

    public static Customer getCustomerByEmail(String email) throws DatabaseException {

        Customer customer;

        String sql = "SELECT * FROM customer WHERE email = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String address = rs.getString("address");
                int postalCode = rs.getInt("postal_code");

                return new Customer(email, firstname, lastname, address, postalCode);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af kunde.", "getCustomerByEmail failed for email=" + email + ": " + e.getMessage());

        }
    }

    public static List<Customer> getAllCustomers() throws DatabaseException {

        List<Customer> users = new ArrayList<>();

        String sql = "SELECT * FROM customer";
        try(Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while(rs.next()) {

                String email = rs.getString("email");
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String address = rs.getString("address");
                int postalCode = rs.getInt("postal_code");

                users.add(new Customer(email, firstname, lastname, address, postalCode));

            }

            return users;
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af kunder.", "getAllCustomers failed: " + e.getMessage());
        }
    }

    public static void registerCustomer(String email, String firstName, String lastName, String address, int postalCode) throws DatabaseException {
        String sql = "INSERT INTO customer (email, firstname, lastname, address, postal_code) VALUES (?, ?, ?, ?, ?)";

        try(Connection connection = ConnectionPool.getInstance().getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, firstName);
            ps.setString(3, lastName);
            ps.setString(4, address);
            ps.setInt(5, postalCode);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke oprette kunden.", "registerCustomer failed for email=" + email + ": " + e.getMessage());
        }
    }

    public static boolean isEmailInSystem(String email) throws DatabaseException {

        String sql = "SELECT 1 FROM customer WHERE email = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved opslag af email.", "isEmailInSystem failed for email = " + email + ": " + e.getMessage());
        }
    }

}
