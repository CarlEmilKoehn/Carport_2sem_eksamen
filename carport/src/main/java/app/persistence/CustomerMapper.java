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

        String sql = "SELECT * FROM public.\"customer\" WHERE email = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String firstname = rs.getString("firstname");
                String lastname = rs.getString("lastname");
                String address = rs.getString("address");
                int postalCode = rs.getInt("postal_code");

                customer = new Customer(email, firstname, lastname, address, postalCode);

                return customer;

            }

            return null;
        } catch (SQLException e) {
            throw new DatabaseException("Could not be connect to DB with getCustomerByEmail: " + e.getMessage());

        }
    }

    public static List<Customer> getAllUsers() throws DatabaseException {

        List<Customer> users = new ArrayList<>();

        String sql = "SELECT * FROM public.\"user\"";
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
            throw new DatabaseException("Could not connect to DB: ", e.getMessage());
        }
    }



}
