package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaterialMapperTest {

    @BeforeAll
    static void initPool() {
        ConnectionPool.getInstance("postgres", "postgres",
                "jdbc:postgresql://localhost:5432/%s?currentSchema=public", "carport");
    }

    private int orderId;
    private int productId;

    @BeforeEach
    void seed() throws Exception {
        try (Connection c = ConnectionPool.getInstance().getConnection()) {

            int unitId;
            try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO unit (unit_name, unit_short_name)
                VALUES ('Styk','Stk')
                ON CONFLICT (unit_short_name) DO UPDATE SET unit_name = EXCLUDED.unit_name
                RETURNING unit_id
            """)) {
                ResultSet rs = ps.executeQuery();
                rs.next();
                unitId = rs.getInt(1);
            } catch (SQLException e) {
                try (PreparedStatement ps = c.prepareStatement("SELECT unit_id FROM unit WHERE unit_short_name='Stk'")) {
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    unitId = rs.getInt(1);
                }
            }

            int categoryId;
            try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO material_category (material_category_name)
                VALUES ('TestKategori')
                ON CONFLICT (material_category_name) DO UPDATE SET material_category_name = EXCLUDED.material_category_name
                RETURNING material_category_id
            """)) {
                ResultSet rs = ps.executeQuery();
                rs.next();
                categoryId = rs.getInt(1);
            } catch (SQLException e) {
                try (PreparedStatement ps = c.prepareStatement("SELECT material_category_id FROM material_category WHERE material_category_name='TestKategori'")) {
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    categoryId = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO material_product (material_category_id, unit_id, length_mm, material_product_name, material_product_description, material_price)
                VALUES (?, ?, 3000, 'TestBræt', 'Test beskrivelse', 100.00)
                RETURNING material_product_id
            """)) {
                ps.setInt(1, categoryId);
                ps.setInt(2, unitId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                productId = rs.getInt(1);
            }

            int roofTypeId;
            try (PreparedStatement ps = c.prepareStatement("""
                SELECT roof_type_id FROM roof_type WHERE roof_type_deg = 0
            """)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    roofTypeId = rs.getInt(1);
                } else {
                    try (PreparedStatement ins = c.prepareStatement("""
                        INSERT INTO roof_type (roof_type_name, roof_type_deg, roof_type_price)
                        VALUES ('Fladt tag', 0, 0.00)
                        RETURNING roof_type_id
                    """)) {
                        ResultSet rs2 = ins.executeQuery();
                        rs2.next();
                        roofTypeId = rs2.getInt(1);
                    }
                }
            }

            try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO customer (email, firstname, lastname, address, postal_code)
                VALUES ('matkunde@fog.dk','Mat','Kunde','Testvej 2',2800)
                ON CONFLICT (email) DO NOTHING
            """)) {
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO customer_order
                (customer_email, order_status, roof_type_id, width_mm, length_mm, height_mm, shed_id, created_at, order_price)
                VALUES ('matkunde@fog.dk','PENDING', ?, 2400, 2400, 2200, NULL, now(), 0.00)
                RETURNING customer_order_id
            """)) {
                ps.setInt(1, roofTypeId);
                ResultSet rs = ps.executeQuery();
                rs.next();
                orderId = rs.getInt(1);
            }

            try (PreparedStatement ps = c.prepareStatement("""
                INSERT INTO order_material (customer_order_id, material_product_id, quantity, note, total_price)
                VALUES (?, ?, 2, 'testnote', 200.00)
            """)) {
                ps.setInt(1, orderId);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }
        }
    }

    @Test
    void getAllMaterialsFromOrder_returnsMaterials() throws DatabaseException {
        List<Material> mats = MaterialMapper.getAllMaterialsFromOrder(orderId);

        assertNotNull(mats);
        assertFalse(mats.isEmpty());

        boolean hasProduct = mats.stream().anyMatch(m -> m.getProductId() == productId);
        assertTrue(hasProduct);

        Material first = mats.stream().filter(m -> m.getProductId() == productId).findFirst().orElseThrow();
        assertEquals(2, first.getQuantity());
        assertEquals(new BigDecimal("200.00"), first.getTotalPrice());
    }
}
