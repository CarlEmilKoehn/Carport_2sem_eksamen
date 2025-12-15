package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

class OrderMapperTest {

    private Order order;
    private final Admin testAdmin = new Admin();
    private final String commentText = "testComment";

    @BeforeEach
    void setSchema() throws SQLException {
        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            connection.setSchema("test");
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void changeOrderStatus() throws DatabaseException {

        String newStatus = "PAID";

        Order olderOrder = order;

        OrderMapper.changeOrderStatus(order.getId(), newStatus);

        assertNotNull(order);
        assertNotEquals(olderOrder.getStatus(), order.getStatus());
        assertEquals(newStatus, order.getStatus());
    }

    @Test
    void changeOrderPrice() throws DatabaseException {

        BigDecimal newPrice = new BigDecimal("55000.55");

        Order oldOrder = order;

        OrderMapper.changeOrderPrice(order.getId(), newPrice, testAdmin, commentText);

        assertNotNull(order);
        assertNotEquals(oldOrder.getTotalPrice(), order.getTotalPrice());
    }

    @Test
    void getAllOrders() throws DatabaseException {

        List<Order> actual = OrderMapper.getAllOrders();

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        boolean found = actual
                .stream()
                .anyMatch(o -> o.getId() == order.getId());
        assertTrue(found);
    }

    @Test
    void getAllCommentsFromOrder() throws DatabaseException {

        OrderMapper.changeOrderPrice(order.getId(), new BigDecimal("21000.00"), testAdmin, commentText);

        List<Comment> actual = OrderMapper.getAllCommentsFromOrder(order.getId());

        assertNotNull(actual);
        assertFalse(actual.isEmpty());

        boolean found = actual
                .stream()
                .anyMatch(c -> commentText.equals(c.getNote()));
        assertTrue(found);
    }
}