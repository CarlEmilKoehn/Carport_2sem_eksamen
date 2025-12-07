package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaterialMapperTest {

    @Test
    void getAllMaterialsFromOrder() {
        int orderId = 1;

        try {
            List<Material> actual = MaterialMapper.getAllMaterialsFromOrder(orderId);

            assertNotNull(actual);
            assertFalse(actual.isEmpty());

            assertTrue(actual.stream().allMatch(m -> m.getOrderId() == orderId));

            Material first = actual.get(0);
            assertNotNull(first.getProductName());
            assertNotNull(first.getUnitShortName());
            assertNotNull(first.getUnitPrice());

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findPostForLength() {

        int minLengthMM = 180;

        try {
            Material actual = MaterialMapper.findPostForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals("97x97 mm. trykimp. Stolpe", actual.getProductName());
            assertEquals("Stolper nedgraves 90 cm. i jord", actual.getProductDescription());
            assertEquals(Integer.valueOf(180), actual.getLengthMM());
            assertEquals(new BigDecimal("82.70"), actual.getUnitPrice());
            assertEquals("Styk", actual.getUnitName());
            assertEquals("Stk", actual.getUnitShortName());

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findRemForLength() {

        int minLengthMM = 310;

        try {
            Material actual = MaterialMapper.findRemForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals("45x195 mm. spærtræ ubh.", actual.getProductName());
            assertEquals(
                    "Remme i sider, saddles ned i stolper (skur del, deles) og/el. spær, monteres på rem",
                    actual.getProductDescription()
            );
            assertEquals(Integer.valueOf(360), actual.getLengthMM());
            assertEquals(new BigDecimal("190.61"), actual.getUnitPrice());
            assertEquals("Styk", actual.getUnitName());
            assertEquals("Stk", actual.getUnitShortName());

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findRafterForLength() {

        int minLengthMM = 500;

        try {
            Material actual = MaterialMapper.findRafterForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals("45x195 mm. spærtræ ubh.", actual.getProductName());
            assertEquals(
                    "Remme i sider, saddles ned i stolper (skur del, deles) og/el. spær, monteres på rem",
                    actual.getProductDescription()
            );
            assertEquals(Integer.valueOf(540), actual.getLengthMM());
            assertEquals(new BigDecimal("285.93"), actual.getUnitPrice());
            assertEquals("Styk", actual.getUnitName());
            assertEquals("Stk", actual.getUnitShortName());

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findUnderSternForLength() {

        int minLengthMM = 310;

        try {
            Material actual = MaterialMapper.findUnderSternForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals("25x200 mm. trykimp. Brædt", actual.getProductName());
            assertEquals(
                    "understernbrædder til for & bag ende og/el. side.",
                    actual.getProductDescription()
            );
            assertEquals(Integer.valueOf(360), actual.getLengthMM());
            assertEquals(new BigDecimal("171.21"), actual.getUnitPrice());
            assertEquals("Styk", actual.getUnitName());
            assertEquals("Stk", actual.getUnitShortName());

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findOverSternForLength() {

        int minLengthMM = 310;

        try {
            Material actual = MaterialMapper.findOverSternForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals("25x125mm. trykimp. Brædt", actual.getProductName());
            assertEquals(
                    "oversternbrædder til forenden og/el. siderne",
                    actual.getProductDescription()
            );
            assertEquals(Integer.valueOf(360), actual.getLengthMM());
            assertEquals(new BigDecimal("125.81"), actual.getUnitPrice());
            assertEquals("Styk", actual.getUnitName());
            assertEquals("Stk", actual.getUnitShortName());

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findRoofSheetForLength() {

        int minLengthMM = 300;

        try {
            Material actual = MaterialMapper.findRoofSheetForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals("Plastmo Ecolite blåtonet", actual.getProductName());
            assertEquals("tagplader monteres på spær", actual.getProductDescription());
            assertEquals(Integer.valueOf(300), actual.getLengthMM());
            assertEquals(new BigDecimal("179.00"), actual.getUnitPrice());
            assertEquals("Styk", actual.getUnitName());
            assertEquals("Stk", actual.getUnitShortName());

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }
}
