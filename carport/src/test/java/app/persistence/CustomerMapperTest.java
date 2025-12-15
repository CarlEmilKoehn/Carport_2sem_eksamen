package app.persistence;

import app.entities.Customer;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    @BeforeAll
    static void initTestConnectionPool() {

        ConnectionPool.reset();

        String user = "postgres";
        String password = "postgres";
        String url = "jdbc:postgresql://localhost:5432/%s";
        String db = "carport";

        ConnectionPool.getInstance(user, password, url, db);
    }

    @BeforeEach
    void setUp() throws SQLException {

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {

            connection.setSchema("test");

            connection.prepareStatement("DELETE FROM customer").executeUpdate();

            String sql = """
            INSERT INTO customer
            (email, firstname, lastname, address, postal_code)
            VALUES (?, ?, ?, ?, ?)
            """;

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "test@test.com");
            ps.setString(2, "Test");
            ps.setString(3, "User");
            ps.setString(4, "Test Street 1");
            ps.setInt(5, 1234);
            ps.executeUpdate();
        }
    }


    @Test
    void testGetCustomerByEmail() throws DatabaseException {

        Customer customer = CustomerMapper.getCustomerByEmail("test@test.com");

        assertNotNull(customer);
        assertEquals("test@test.com", customer.getEmail());
        assertEquals("Test", customer.getFirstName());
        assertEquals("User", customer.getLastName());
        assertEquals("Test Street 1", customer.getAddress());
        assertEquals(1234, customer.getPostalCode());
    }

    @Test
    void testGetCustomerByEmailNotFound() throws DatabaseException {

        Customer customer = CustomerMapper.getCustomerByEmail("missing@test.com");

        assertNull(customer);
    }

    @Test
    void testRegisterCustomer() throws DatabaseException {

        CustomerMapper.registerCustomer(
                "new@test.com",
                "New",
                "Customer",
                "New Street 5",
                5678
        );

        Customer customer = CustomerMapper.getCustomerByEmail("new@test.com");

        assertNotNull(customer);
        assertEquals("New", customer.getFirstName());
        assertEquals("Customer", customer.getLastName());
        assertEquals("New Street 5", customer.getAddress());
        assertEquals(5678, customer.getPostalCode());
    }

    @Test
    void testGetAllCustomers() throws DatabaseException {

        List<Customer> customers = CustomerMapper.getAllCustomers();

        assertEquals(1, customers.size());
        assertTrue(
                customers.stream()
                        .anyMatch(c -> c.getEmail().equals("test@test.com"))
        );

    }

    @Test
    void testIsEmailInSystemTrue() throws DatabaseException {
        assertTrue(CustomerMapper.isEmailInSystem("test@test.com"));
    }

    @Test
    void testIsEmailInSystemFalse() throws DatabaseException {
        assertFalse(CustomerMapper.isEmailInSystem("unknown@test.com"));
    }

    @AfterAll
    static void tearDown() {
        ConnectionPool.reset();
    }

}

