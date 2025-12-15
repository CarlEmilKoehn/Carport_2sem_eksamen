package app.persistence;

import app.entities.Customer;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

class CustomerMapperTest {

    @BeforeAll
    static void initPool() {
        ConnectionPool.getInstance("postgres", "postgres",
                "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "carport");
    }

    @BeforeEach
    void cleanup() throws Exception {
        try (Connection c = ConnectionPool.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM customer WHERE email = ?")) {
            ps.setString(1, "testkunde@fog.dk");
            ps.executeUpdate();
        }
    }

    @Test
    void registerCustomer_and_getCustomerByEmail() throws DatabaseException {
        CustomerMapper.registerCustomer("testkunde@fog.dk", "Test", "Kunde", "Testvej 1", 2800);

        Customer c = CustomerMapper.getCustomerByEmail("testkunde@fog.dk");
        assertNotNull(c);
        assertEquals("testkunde@fog.dk", c.getEmail());
        assertEquals("Test", c.getFirstName());
        assertEquals("Kunde", c.getLastName());
        assertEquals("Testvej 1", c.getAddress());
        assertEquals(2800, c.getPostalCode());
    }

    @Test
    void isEmailInSystem_true_false() throws DatabaseException {
        assertFalse(CustomerMapper.isEmailInSystem("testkunde@fog.dk"));

        CustomerMapper.registerCustomer("testkunde@fog.dk", "Test", "Kunde", "Testvej 1", 2800);

        assertTrue(CustomerMapper.isEmailInSystem("testkunde@fog.dk"));
    }

    @Test
    void getAllCustomers_containsInserted() throws DatabaseException {
        CustomerMapper.registerCustomer("testkunde@fog.dk", "Test", "Kunde", "Testvej 1", 2800);

        boolean found = CustomerMapper.getAllCustomers().stream()
                .anyMatch(c -> "testkunde@fog.dk".equalsIgnoreCase(c.getEmail()));

        assertTrue(found);
    }
}
