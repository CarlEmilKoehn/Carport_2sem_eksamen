package app.persistence;

import app.entities.Admin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;

public class AdminMapperTest {

    private static final String TEST_ADMIN_EMAIL = "admin@test.com";
    private static final String TEST_ADMIN_PASSWORD = "admintest123";
    private static final String TEST_ADMIN_FIRSTNAME = "Admin";
    private static final String TEST_ADMIN_LASTNAME = "Test";

    @BeforeAll
    static void initTestConnectionPool() {
        ConnectionPool.reset();

        ConnectionPool.getInstance(
                "postgres",
                "postgres",
                "jdbc:postgresql://localhost:5432/%s",
                "carport"
        );
    }

    @BeforeEach
    void beforeTests() throws SQLException {
        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            connection.setSchema("test");
            connection.prepareStatement("DELETE FROM admin").executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}











