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

    @Test
    @DisplayName("Test af oprettelse af en ny admin")
    void testCreateAdmin() throws DatabaseException {

        Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin);

        Admin hentetAdmin = AdminMapper.getAdminByEmail(TEST_ADMIN_EMAIL);
        assertNotNull(hentetAdmin);

        assertEquals(TEST_ADMIN_EMAIL, hentetAdmin.getAdminEmail());
        assertEquals(TEST_ADMIN_FIRSTNAME, hentetAdmin.getAdminFirstname());
        assertEquals(TEST_ADMIN_LASTNAME, hentetAdmin.getAdminLastname());
    }

    @Test
    @DisplayName("Test login")
    void testAdminLogin() throws DatabaseException {

        Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin);

        Admin loggedInAdmin = AdminMapper.login(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD);

        assertNotNull(loggedInAdmin);
        assertEquals(TEST_ADMIN_EMAIL, loggedInAdmin.getAdminEmail());
        assertEquals(TEST_ADMIN_FIRSTNAME, loggedInAdmin.getAdminFirstname());
        assertEquals(TEST_ADMIN_LASTNAME, loggedInAdmin.getAdminLastname());
    }

    @Test
    @DisplayName("Test login med ugyldigt password")
    void testLoginError() throws DatabaseException {
        Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin);

        assertThrows(DatabaseException.class, () -> {
            AdminMapper.login(TEST_ADMIN_EMAIL, "Forkert adgangskode");
        });
    }

    @Test
    @DisplayName("Test hentning af alle admins")
    void testGetAllAdmins() throws DatabaseException {

        Admin admin1 = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin1);

        List<Admin> admins = AdminMapper.getAllAdmins();

        assertFalse(admins.isEmpty());

       boolean adminVerificeret = false;
       for (Admin admin : admins) {
           if (admin.getAdminEmail().equals(TEST_ADMIN_EMAIL)) {
               adminVerificeret = true;
               break;
           }
       }
       assertTrue(adminVerificeret);
    }
@Test
@DisplayName("Test opdatering af admin")
    void testUpdateAdmin() throws DatabaseException {

        Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin);

        admin.setAdminPassword("nytPassword123");
        admin.setAdminFirstname("Nyt");
        admin.setAdminLastname("Navn");
        AdminMapper.updateAdmin(admin);

        Admin opdateretAdmin = AdminMapper.getAdminByEmail(TEST_ADMIN_EMAIL);
        assertEquals("Nyt", opdateretAdmin.getAdminFirstname());
        assertEquals("Navn", opdateretAdmin.getAdminLastname());
    }

@Test
@DisplayName("Test sletning af admin")
void testRemoveAdmin() throws DatabaseException {

    Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
    AdminMapper.createAdmin(admin);
    AdminMapper.deleteAdmin(TEST_ADMIN_EMAIL);

    assertThrows(DatabaseException.class, () -> {
        AdminMapper.getAdminByEmail(TEST_ADMIN_EMAIL);
    });
}

@Test
@DisplayName("Admin findes ikke.")
void testGetAdminWithWrongEmail() {

        assertThrows(DatabaseException.class, () -> {
            AdminMapper.getAdminByEmail("Ugyldig email");
        });
    }

    @AfterAll
    static void shutdownPool() {
        ConnectionPool.reset();
    }

}

