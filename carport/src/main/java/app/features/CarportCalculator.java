package app.features;

import app.entities.Order;
import app.entities.OrderWithShed;

import java.math.BigDecimal;

public class CarportCalculator {

    public static BigDecimal calculate(Order order) {

        BigDecimal price = new BigDecimal("0");

        price = price.add(calculateRoofPrice(order));

        price = price.add(calculateCarportPrice(order));

        price = price.add(calculateShedPrice(order));

        return price;
    }

    private static BigDecimal calculateRoofPrice(Order order) {
        return BigDecimal.valueOf(order.getRoofType().getDegrees() * 240.00);
    }

    private static BigDecimal calculateCarportPrice(Order order) {

        int carportWidthMM = order.getWidthMM();
        int carportHeightMM = order.getHeightMM();

        return BigDecimal.valueOf(2.00 * (carportWidthMM + carportHeightMM) / 1000.00);
    }

    private static BigDecimal calculateShedPrice(Order order) {

        if (order instanceof OrderWithShed ows) {

            int shedWidthMM = ows.getShed().getWidthMM();
            int shedHeightMM = ows.getShed().getLengthMM();

            return BigDecimal.valueOf(2.00 * (shedWidthMM + shedHeightMM) / 1000.00);
        }

        return BigDecimal.valueOf(0.00);
    }
}