package app.persistence;

import app.entities.Admin;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminMapper {

    public static Admin login(String email, String password) throws DatabaseException {
        String sql = "SELECT * FROM admin WHERE admin_email = ? AND admin_password = ?";

        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String firstname = rs.getString("admin_firstname");
                String lastname = rs.getString("admin_lastname");

                return new Admin(email, password, firstname, lastname);
            }

            return null;

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved login lige nu.", "login SQL failed for email=" + email + ": " + e.getMessage());
        }
    }
}
