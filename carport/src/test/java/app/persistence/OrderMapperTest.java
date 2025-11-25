package app.persistence;

import app.entities.Order;
import app.entities.OrderWithShed;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

class OrderMapperTest {

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllOrders() throws DatabaseException {

        //--- given ---
        List<Order> expected = List.of(
                new Order(1, "a@a.com", "PENDING", 100, 200, Timestamp.valueOf("2024-01-01 10:00:00"), new BigDecimal("123.45")),
                new Order(2, "b@b.com", "PAID", 300, 400, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78")),
                new OrderWithShed(3, "c@c.com", "PENDING", 300, 400, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78"), 150, 250),
                new OrderWithShed(4, "d@d.com", "PAID", 200, 100, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78"), 150, 250)
        );

        //--- when ---
        List<Order> actual = OrderMapper.getAllOrders();

        //--- then ---
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }

    @Test
    void getAllOrdersByEmail() throws DatabaseException {

        //--- given ---
        List<Order> expected = List.of(
                new Order(1, "a@a.com", "PENDING", 100, 200, Timestamp.valueOf("2024-01-01 10:00:00"), new BigDecimal("123.45")),
                new Order(2, "d@d.com", "PAID", 300, 400, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78")),
                new OrderWithShed(3, "c@c.com", "PENDING", 300, 400, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78"), 150, 250),
                new OrderWithShed(4, "d@d.com", "PAID", 200, 100, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78"), 150, 250)
        );

        //--- when ---
        List<Order> actual = OrderMapper.getAllOrdersByEmail("d@d");

        //--- then ---
        assertEquals(expected, actual);
        assertEquals(2, actual.size());
    }
}