package app.services;

import app.entities.*;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CarportCalculatorServiceTest {

    @BeforeAll
    static void initTestSchema() throws Exception {
        try (Connection c = ConnectionPool.getInstance().getConnection();
             Statement stmt = c.createStatement()) {

            stmt.execute("SET search_path TO test");
        }
    }

    @BeforeEach
    void ensureTestSchemaPerTest() throws Exception {
        try (Connection c = ConnectionPool.getInstance().getConnection();
             Statement stmt = c.createStatement()) {

            stmt.execute("SET search_path TO test");
        }
    }

    @Test
    void calculate_basicCarport_withoutShed() throws DatabaseException {

        Order order = new Order(
                1,
                "test@test.dk",
                "PENDING",
                null,
                3000,
                2400,
                6000,
                new Timestamp(System.currentTimeMillis()),
                new ArrayList<>(),
                new ArrayList<>(),
                BigDecimal.ZERO
        );

        CarportCalculatorService.calculate(order);

        assertNotNull(order.getMaterials());
        assertFalse(order.getMaterials().isEmpty());

        assertTrue(order.getMaterials().stream()
                .anyMatch(m -> m.getNote().contains("Stolper")));

        assertTrue(order.getMaterials().stream()
                .anyMatch(m -> m.getNote().contains("Ydre og inderrem")));

        assertTrue(order.getMaterials().stream()
                .anyMatch(m -> m.getNote().contains("Spær")));

        assertTrue(order.getMaterials().stream()
                .anyMatch(m -> m.getNote().contains("Tagplader")));

        assertTrue(order.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculate_carportWithShed_addsCladding() throws DatabaseException {

        Shed shed = new Shed(2100, 3000);

        OrderWithShed order = new OrderWithShed(
                2,
                "test@test.dk",
                "PENDING",
                null,
                3000,
                2400,
                6000,
                new Timestamp(System.currentTimeMillis()),
                new ArrayList<>(),
                new ArrayList<>(),
                BigDecimal.ZERO,
                shed
        );

        CarportCalculatorService.calculate(order);

        assertTrue(order.getMaterials().stream()
                .anyMatch(m -> m.getNote().contains("Beklædning")));
    }

    @Test
    void calculate_withRoofType_addsSlopePrice() throws DatabaseException {

        RoofType roofType = new RoofType(
                1,
                "Test roof",
                30,
                BigDecimal.ZERO
        );

        Order order = new Order(
                3,
                "test@test.dk",
                "PENDING",
                roofType,
                3000,
                2400,
                6000,
                new Timestamp(System.currentTimeMillis()),
                new ArrayList<>(),
                new ArrayList<>(),
                BigDecimal.ZERO
        );

        CarportCalculatorService.calculate(order);

        BigDecimal expectedSlope =
                BigDecimal.valueOf(30).multiply(BigDecimal.valueOf(240));

        assertTrue(order.getTotalPrice().compareTo(expectedSlope) > 0);
    }
}
