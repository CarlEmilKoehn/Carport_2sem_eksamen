package app.services;

import app.entities.*;
import app.exceptions.DatabaseException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CarportCalculatorServiceTest {

    /*
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
        assertNotNull(order.getTotalPrice());
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
        assertNotNull(order.getTotalPrice());
        assertTrue(order.getTotalPrice().compareTo(BigDecimal.ZERO) > 0);
    }

     */
}
