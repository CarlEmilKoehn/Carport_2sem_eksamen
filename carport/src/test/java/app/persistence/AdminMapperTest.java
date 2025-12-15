package app.persistence;

import app.entities.Admin;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

class AdminMapperTest {

    @BeforeAll
    static void initPool() {
        ConnectionPool.getInstance("postgres", "postgres",
                "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "carport");
    }

    @BeforeEach
    void seedAdmin() throws Exception {
        try (Connection c = ConnectionPool.getInstance().getConnection()) {
            try (PreparedStatement del = c.prepareStatement("DELETE FROM admin WHERE admin_email = ?")) {
                del.setString(1, "testadmin@fog.dk");
                del.executeUpdate();
            }
            try (PreparedStatement ins = c.prepareStatement("""
                INSERT INTO admin (admin_email, admin_password, admin_firstname, admin_lastname)
                VALUES (?, ?, ?, ?)
            """)) {
                ins.setString(1, "testadmin@fog.dk");
                ins.setString(2, "admin123");
                ins.setString(3, "Test");
                ins.setString(4, "Admin");
                ins.executeUpdate();
            }
        }
    }

    @Test
    void login_ok() throws DatabaseException {
        Admin admin = AdminMapper.login("testadmin@fog.dk", "admin123");
        assertNotNull(admin);
        assertEquals("testadmin@fog.dk", admin.getAdminEmail());
    }

    @Test
    void login_fail() throws DatabaseException {
        Admin admin = AdminMapper.login("testadmin@fog.dk", "forkert");
        assertNull(admin);
    }
}
