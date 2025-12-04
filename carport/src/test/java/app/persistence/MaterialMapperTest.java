package app.persistence;

import app.entities.Material;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MaterialMapperTest {

    @Test
    void getAllMaterialsFromOrder() {

        int orderId = 1;

        List<Material> expected = new ArrayList<>();

        expected.add(new Material(

        ));

        expected.add(new Material(

        ));

        try {
            List<Material> actual = MaterialMapper.getAllMaterialsFromOrder(orderId);

            assertNotNull(actual);
            assertEquals(expected.size(), actual.size());
            assertEquals(expected, actual);

        } catch (DatabaseException e) {
            assert false;
        }
    }

    @Test
    void findPostForLength() {

        int minLengthMM = 180;

        Material expected = new Material(
                42,
                "97x97 mm. trykimp. Stolpe",
                "Stolper nedgraves 90 cm. i jord",
                180,
                new BigDecimal("82.70"),
                "Styk",
                "Stk"
        );

        try {
            Material actual = MaterialMapper.findPostForLength(300);

            assertNotNull(actual);
            assertEquals(expected, actual);

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findRemForLength() {

        int minLengthMM;

        Material expected = new Material(

        );

        try {
            Material actual = MaterialMapper.findRemForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals(expected, actual);

        } catch (DatabaseException e) {
            assert false;
        }
    }

    @Test
    void findRafterForLength() {

        int minLengthMM;

        Material expected = new Material(

        );

        try {
            Material actual = MaterialMapper.findRafterForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals(expected, actual);

        } catch (DatabaseException e) {
            assert false;
        }
    }

    @Test
    void findUnderSternForLength() {

        int minLengthMM = 310;

        Material expected = new Material(
                2,
                "25x200 mm. trykimp. Brædt",
                "understernbrædder til for & bag ende og/el. side.",
                360,
                new BigDecimal("171.21"),
                "Styk",
                "Stk"
        );

        try {
            Material actual = MaterialMapper.findUnderSternForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals(expected, actual);

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findOverSternForLength() {

        int minLengthMM = 310;

        Material expected = new Material(
                8,
                "25x125mm. trykimp. Brædt",
                "oversternbrædder til forenden og/el. siderne",
                360,
                new BigDecimal("125.81"),
                "Styk",
                "Stk"
        );

        try {
            Material actual = MaterialMapper.findOverSternForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals(expected, actual);

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findRoofSheetForLength() {

        int minLengthMM = 300;

        Material expected = new Material(
                63,
                "Plastmo Ecolite blåtonet",
                "tagplader monteres på spær",
                300,
                new BigDecimal("179.00"),
                "Styk",
                "Stk"
        );

        try {
            Material actual = MaterialMapper.findRoofSheetForLength(minLengthMM);

            assertNotNull(actual);
            assertEquals(expected, actual);

        } catch (DatabaseException e) {
            fail(e.getMessage());
        }
    }
}