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
    public static void createAdmin(Admin admin, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "INSERT INTO admin (admin_email, admin_password, admin_firstname, admin_lastname) VALUES (?, ?, ?, ?)";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, admin.getAdminEmail());
            ps.setString(2, admin.getAdminPassword());
            ps.setString(3, admin.getAdminFirstname());
            ps.setString(4, admin.getAdminLastname());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Kunne ikke oprette admin i databasen: " + e.getMessage());
        }
    }

    public static Admin getAdminByEmail(String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM admin WHERE admin_email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String firstname = rs.getString("admin_firstname");
                String lastname = rs.getString("admin_lastname");
                return new Admin(email, null, firstname, lastname);
            } else {
                throw new DatabaseException("Kunne ikke hente admin med email: " + email + " fra databasen");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af admin: " + e.getMessage());
            //Inset values and atddtitions
        }
    }

    public static List<Admin> getAllAdmins(ConnectionPool connectionPool) throws DatabaseException {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String email = rs.getString("admin_email");
                String firstname = rs.getString("admin_firstname");
                String lastname = rs.getString("admin_lastname");
                admins.add(new Admin(email, firstname, lastname));
            }
            return admins;
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af alle admins fra databasen: " + e.getMessage());
        }
    }

    public static void updateAdmin(Admin admin, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "UPDATE admin SET admin_password = ?, admin_firstname = ?, admin_lastname = ? WHERE admin_email = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, admin.getAdminPassword());
            ps.setString(2, admin.getAdminFirstname());
            ps.setString(3, admin.getAdminLastname());
            ps.setString(4, admin.getAdminEmail());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved opdatering af admin: " + e.getMessage());
        }
    }

    public static void deleteAdmin(String email, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "DELETE FROM admin WHERE admin_email = ?";
        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved sletning af admin med email: " + email + " fra databasen");
        }
    }
    public static Admin login(String email, String password, ConnectionPool connectionPool) throws DatabaseException {
        String sql = "SELECT * FROM admin WHERE admin_email = ? AND admin_password = ?";

        try (Connection connection = connectionPool.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String firstname = rs.getString("admin_firstname");
                String lastname = rs.getString("admin_lastname");
                    return new Admin(email, firstname, lastname);

            } else {
                throw new DatabaseException("Ugyldig email eller password");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Fejl ved hentning af admin med email: " + email + " fra databasen");
        }
    }
}
