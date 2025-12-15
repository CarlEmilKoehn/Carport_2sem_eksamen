package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaterialMapperTest {

    @BeforeAll
    static void initTestConnectionPool() {
        ConnectionPool.reset();

        ConnectionPool.getInstance(
                "postgres",
                "postgres",
                "jdbc:postgresql://localhost:5432/%s",
                "carport"
        );
    }

    @BeforeEach
    void setSchema() throws SQLException {
        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            connection.setSchema("test");
        }
    }

    @Test
    void getAllMaterialsFromOrder() throws DatabaseException {
        int orderId = 1;

        List<Material> actual = MaterialMapper.getAllMaterialsFromOrder(orderId);

        assertNotNull(actual);
        assertFalse(actual.isEmpty());
        assertTrue(actual.stream().allMatch(m -> m.getOrderId() == orderId));
    }

    @Test
    void findPostForLength() throws DatabaseException {
        Material actual = MaterialMapper.findPostForLength(180);

        assertNotNull(actual);
        assertEquals("97x97 mm. trykimp. Stolpe", actual.getProductName());
        assertEquals(Integer.valueOf(180), actual.getLengthMM());
        assertEquals(new BigDecimal("82.70"), actual.getUnitPrice());
    }

    @Test
    void findRemForLength() throws DatabaseException {
        Material actual = MaterialMapper.findRemForLength(310);

        assertNotNull(actual);
        assertEquals(Integer.valueOf(360), actual.getLengthMM());
    }

    @Test
    void findRafterForLength() throws DatabaseException {
        Material actual = MaterialMapper.findRafterForLength(500);

        assertNotNull(actual);
        assertEquals(Integer.valueOf(540), actual.getLengthMM());
    }

    @Test
    void findUnderSternForLength() throws DatabaseException {
        Material actual = MaterialMapper.findUnderSternForLength(310);

        assertNotNull(actual);
        assertEquals(Integer.valueOf(360), actual.getLengthMM());
    }

    @Test
    void findOverSternForLength() throws DatabaseException {
        Material actual = MaterialMapper.findOverSternForLength(310);

        assertNotNull(actual);
        assertEquals(Integer.valueOf(360), actual.getLengthMM());
    }

    @Test
    void findRoofSheetForLength() throws DatabaseException {
        Material actual = MaterialMapper.findRoofSheetForLength(300);

        assertNotNull(actual);
        assertEquals(Integer.valueOf(300), actual.getLengthMM());
    }

    @AfterAll
    static void shutdownPool() {
        ConnectionPool.reset();
    }
}
