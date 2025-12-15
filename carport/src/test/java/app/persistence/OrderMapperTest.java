package app.persistence;

import app.entities.Admin;
import app.entities.Order;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    @BeforeAll
    static void initPool() {
        ConnectionPool.getInstance("postgres", "postgres",
                "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "carport");
    }

    private int roofTypeId;
    private int orderId;

    @BeforeEach
    void seed() throws Exception {
        try (Connection c = ConnectionPool.getInstance().getConnection()) {

            try (PreparedStatement upsertCustomer = c.prepareStatement("""
                INSERT INTO customer (email, firstname, lastname, address, postal_code)
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT (email) DO UPDATE
                SET firstname = EXCLUDED.firstname,
                    lastname = EXCLUDED.lastname,
                    address = EXCLUDED.address,
                    postal_code = EXCLUDED.postal_code
            """)) {
                upsertCustomer.setString(1, "kunde@fog.dk");
                upsertCustomer.setString(2, "Kunde");
                upsertCustomer.setString(3, "Test");
                upsertCustomer.setString(4, "Testvej 1");
                upsertCustomer.setInt(5, 2800);
                upsertCustomer.executeUpdate();
            }

            try (PreparedStatement findRoof = c.prepareStatement("""
                SELECT roof_type_id FROM roof_type WHERE roof_type_deg = 0
            """)) {
                ResultSet rs = findRoof.executeQuery();
                if (rs.next()) {
                    roofTypeId = rs.getInt(1);
                } else {
                    try (PreparedStatement insRoof = c.prepareStatement("""
                        INSERT INTO roof_type (roof_type_name, roof_type_deg, roof_type_price)
                        VALUES ('Fladt tag', 0, 0.00)
                        RETURNING roof_type_id
                    """)) {
                        ResultSet rs2 = insRoof.executeQuery();
                        rs2.next();
                        roofTypeId = rs2.getInt(1);
                    }
                }
            }

            try (PreparedStatement upsertAdmin = c.prepareStatement("""
                INSERT INTO admin (admin_email, admin_password, admin_firstname, admin_lastname)
                VALUES (?, ?, ?, ?)
                ON CONFLICT (admin_email) DO NOTHING
            """)) {
                upsertAdmin.setString(1, "testadmin@fog.dk");
                upsertAdmin.setString(2, "admin123");
                upsertAdmin.setString(3, "Test");
                upsertAdmin.setString(4, "Admin");
                upsertAdmin.executeUpdate();
            }

            try (PreparedStatement insOrder = c.prepareStatement("""
                INSERT INTO customer_order
                (customer_email, order_status, roof_type_id, width_mm, length_mm, height_mm, shed_id, order_price)
                VALUES (?, ?, ?, ?, ?, ?, NULL, ?)
                RETURNING customer_order_id
            """)) {
                insOrder.setString(1, "kunde@fog.dk");
                insOrder.setString(2, "PENDING");
                insOrder.setInt(3, roofTypeId);
                insOrder.setInt(4, 2400);
                insOrder.setInt(5, 2400);
                insOrder.setInt(6, 2200);
                insOrder.setBigDecimal(7, new BigDecimal("1234.00"));
                ResultSet rs = insOrder.executeQuery();
                rs.next();
                orderId = rs.getInt(1);
            }
        }
    }

    @Test
    void getOrderById_ok() throws DatabaseException {
        Order o = OrderMapper.getOrderByOrderId(orderId);
        assertNotNull(o);
        assertEquals(orderId, o.getId());
        assertEquals("kunde@fog.dk", o.getEmail());
    }

    @Test
    void changeOrderPrice_creates_history_and_updates_price() throws DatabaseException {
        Admin admin = new Admin("testadmin@fog.dk", "admin123", "Test", "Admin");
        OrderMapper.changeOrderPrice(orderId, new BigDecimal("9999.99"), admin, "test note");

        Order o = OrderMapper.getOrderByOrderId(orderId);
        assertNotNull(o);
        assertEquals(new BigDecimal("9999.99"), o.getTotalPrice());
    }
}
