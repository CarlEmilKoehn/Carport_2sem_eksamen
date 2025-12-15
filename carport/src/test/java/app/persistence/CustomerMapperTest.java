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

    @BeforeEach
    void setUp() throws SQLException {

        try (Connection connection = ConnectionPool.getInstance().getConnection()) {

            connection.prepareStatement("DELETE FROM test.user").executeUpdate();

            String sql = """
                INSERT INTO test.user
                (user_email, user_firstname, user_lastname, user_adress, user_postal_code)
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
        assertEquals("test@test.com", customers.get(0).getEmail());
    }

    @Test
    void testIsEmailInSystemTrue() throws DatabaseException {
        assertTrue(CustomerMapper.isEmailInSystem("test@test.com"));
    }

    @Test
    void testIsEmailInSystemFalse() throws DatabaseException {
        assertFalse(CustomerMapper.isEmailInSystem("unknown@test.com"));
    }
}

