package app.services;

import app.entities.*;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CarportCalculatorServiceTest {

    @BeforeAll
    static void initPool() {
        ConnectionPool.getInstance(
                "postgres",
                "postgres",
                "jdbc:postgresql://localhost:5432/%s?currentSchema=test",
                "carport"
        );
    }

    @BeforeEach
    void seedMaterials() throws Exception {

        try (Connection c = ConnectionPool.getInstance().getConnection();
             Statement st = c.createStatement()) {

            st.execute("SET search_path TO test");

            // Unit
            st.execute("""
    INSERT INTO material_product
    (material_category_id, unit_id, length_mm,
     material_product_name, material_product_description, material_price)
    VALUES
    -- Stolper (>= height 2200)
    ((SELECT material_category_id FROM material_category WHERE material_category_name='Stolper'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     2400, 'Stolpe', 'Test stolpe', 50),

    -- Rem / Spær (>= width)
    ((SELECT material_category_id FROM material_category WHERE material_category_name='Rem/Spær'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     3600, 'Rem', 'Test rem', 75),

    -- Understern (>= total length incl shed)
    ((SELECT material_category_id FROM material_category WHERE material_category_name='Understern'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     6000, 'Understern', 'Test understern', 60),

    -- Overstern (>= total length incl shed)
    ((SELECT material_category_id FROM material_category WHERE material_category_name='Overstern'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     6000, 'Overstern', 'Test overstern', 65),

    -- Tagplader (>= length)
    ((SELECT material_category_id FROM material_category WHERE material_category_name='Tagplader'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     3000, 'Tagplade', 'Test tag', 100),

    -- Beklædning (>= height)
    ((SELECT material_category_id FROM material_category WHERE material_category_name='Beklædning'),
     (SELECT unit_id FROM unit WHERE unit_short_name='Stk'),
     2400, 'Beklædning', 'Test beklædning', 55)
""");

        }
    }



    @Test
    void calculate_basicOrder_setsMaterials_and_price() throws DatabaseException {

        RoofType roof = new RoofType(1, "Fladt tag", 0, BigDecimal.ZERO);

        Order order = new Order(
                "calc@fog.dk",
                "PENDING",
                roof,
                2400,
                2200,
                2400,
                new ArrayList<>(),
                null,
                BigDecimal.ZERO
        );

        CarportCalculatorService.calculate(order);

        assertNotNull(order.getMaterials());
        assertFalse(order.getMaterials().isEmpty());
        assertTrue(order.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void calculate_orderWithShed_setsMaterials_and_price() throws DatabaseException {

        RoofType roof = new RoofType(1, "Fladt tag", 0, BigDecimal.ZERO);

        Order order = new OrderWithShed(
                "calc2@fog.dk",
                "PENDING",
                roof,
                3000,
                2200,
                5400,
                new ArrayList<>(),
                null,
                BigDecimal.ZERO,
                new Shed(2100, 2400)
        );

        CarportCalculatorService.calculate(order);

        assertNotNull(order.getMaterials());
        assertFalse(order.getMaterials().isEmpty());
        assertTrue(order.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
    }
}
