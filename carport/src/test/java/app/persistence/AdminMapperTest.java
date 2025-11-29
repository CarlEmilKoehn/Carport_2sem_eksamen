package app.persistence;

import app.entities.Admin;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

public class AdminMapperTest {

    private static ConnectionPool connectionPool;
    private static final String TEST_ADMIN_EMAIL = "admin@test.com";
    private static final String TEST_ADMIN_PASSWORD = "admintest123";
    private static final String TEST_ADMIN_FIRSTNAME = "Admin";
    private static final String TEST_ADMIN_LASTNAME = "Test";

    @BeforeAll
    static void initialiserConnectionPool() {
        connectionPool = ConnectionPool.getInstance();
    }

    @BeforeEach
    void oprydningFørTest() throws DatabaseException {
        try {
            AdminMapper.deleteAdmin(TEST_ADMIN_EMAIL, connectionPool);
        } catch (DatabaseException e) {

        }
    }

    @AfterEach
    void sletTestAdmin() throws DatabaseException {
        try {
            AdminMapper.deleteAdmin(TEST_ADMIN_EMAIL, connectionPool);
        } catch (DatabaseException e) {

        }
    }

    @Test
    @DisplayName("Test af oprettelse af en ny admin")
    void testOpretAdmin() throws DatabaseException {

        Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin, connectionPool);

        Admin hentetAdmin = AdminMapper.getAdminByEmail(TEST_ADMIN_EMAIL, connectionPool);
        assertNotNull(hentetAdmin);

        assertEquals(TEST_ADMIN_EMAIL, hentetAdmin.getAdminEmail());
        assertEquals(TEST_ADMIN_FIRSTNAME, hentetAdmin.getAdminFirstname());
        assertEquals(TEST_ADMIN_LASTNAME, hentetAdmin.getAdminLastname());
    }

    @Test
    @DisplayName("Test login")
    void testLoginSuccess() throws DatabaseException {

        Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin, connectionPool);

        Admin loggedInAdmin = AdminMapper.login(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, connectionPool);

        assertNotNull(loggedInAdmin);
        assertEquals(TEST_ADMIN_EMAIL, loggedInAdmin.getAdminEmail());
        assertEquals(TEST_ADMIN_FIRSTNAME, loggedInAdmin.getAdminFirstname());
        assertEquals(TEST_ADMIN_LASTNAME, loggedInAdmin.getAdminLastname());
    }

    @Test

    @DisplayName("Test login med ugyldigt password")
    void testLoginFejl() throws DatabaseException {
        Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin, connectionPool);

        assertThrows(DatabaseException.class, () -> {
            AdminMapper.login(TEST_ADMIN_EMAIL, "Forkert adgangskode", connectionPool);
        });
    }
    @Test
    @DisplayName("Test hentning af alle admins")
    void testHentAlleAdmins() throws DatabaseException {

        Admin admin1 = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
        AdminMapper.createAdmin(admin1, connectionPool);

        List<Admin> admins = AdminMapper.getAllAdmins(connectionPool);

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
        AdminMapper.createAdmin(admin, connectionPool);

      admin.setAdminPassword("nytPassword123");
      admin.setAdminFirstname("Nyt");
      admin.setAdminLastname("Navn");
      AdminMapper.updateAdmin(admin, connectionPool);

      Admin opdateretAdmin = AdminMapper.getAdminByEmail(TEST_ADMIN_EMAIL, connectionPool);
      assertEquals("Nyt", opdateretAdmin.getAdminFirstname());
      assertEquals("Navn", opdateretAdmin.getAdminLastname());
}

@Test
@DisplayName("Test sletning af admin")
void testSletAdmin() throws DatabaseException {

    Admin admin = new Admin(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, TEST_ADMIN_FIRSTNAME, TEST_ADMIN_LASTNAME);
    AdminMapper.createAdmin(admin, connectionPool);
    AdminMapper.deleteAdmin(TEST_ADMIN_EMAIL, connectionPool);

    assertThrows(DatabaseException.class, () -> {
        AdminMapper.getAdminByEmail(TEST_ADMIN_EMAIL, connectionPool);
    });
}

@Test
@DisplayName("Admin findes ikke.")
void testGetAdminMedUgyldigEmail() {
    assertThrows(DatabaseException.class, () -> {
        AdminMapper.getAdminByEmail("Ugyldig email", connectionPool);
    });
}
}

