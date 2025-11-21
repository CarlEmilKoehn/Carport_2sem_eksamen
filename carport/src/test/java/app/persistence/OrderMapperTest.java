package app.persistence;

import app.entities.Order;
import app.entities.OrderWithShed;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

class OrderMapperTest {

    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {

        orderMapper = new OrderMapper();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getAllOrders() {

        //--- given ---
        List<Order> expected = List.of(
                new Order(1, "a@a.com", "PENDING", 100, 200, Timestamp.valueOf("2024-01-01 10:00:00"), new BigDecimal("123.45")),
                new Order(2, "b@b.com", "PAID", 300, 400, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78")),
                new OrderWithShed(3, "c@c.com", "PENDING", 300, 400, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78"), 150, 250),
                new OrderWithShed(4, "d@d.com", "PAID", 200, 100, Timestamp.valueOf("2024-02-01 12:00:00"), new BigDecimal("456.78"), 150, 250)
        );

        //--- when ---
        List<Order> actual = orderMapper.getAllOrders();

        //--- then ---
        assertEquals(expected, actual);
        assertEquals(expected.size(), actual.size());
    }
}