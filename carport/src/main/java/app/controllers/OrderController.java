package app.controllers;

import app.entities.*;
import app.exceptions.DatabaseException;
import app.persistence.CustomerMapper;
import app.persistence.OrderMapper;
import app.services.CarportCalculatorService;
import app.services.email.CarportMailService;
import app.services.email.MailSender;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderController {

    public static void guardAgainstOrderEqualsNull(Order order) {
        if (order == null) {
            return;
        }
    }

    public static void addRoutes(Javalin app) {

        app.post("submitRequestForCarport", ctx -> {

            Order order = handleCreateOrder(ctx);
            guardAgainstOrderEqualsNull(order);

            boolean mailOK = handleRequestCarportEmail(order);

            if (mailOK) {
                ctx.status(200).result("Order created: " + order.getId());
            } else {
                ctx.status(200).result("Order created: " + order.getId() + " (but email failed)");
            }
        });

        //TODO
        app.post("submitPricingForOrder", ctx -> {

            Order order = ctx.attribute("order");

            guardAgainstOrderEqualsNull(order);

            boolean mailOK = handlePricingCarportEmail(order);

            if (mailOK) {
                ctx.status(200).result("Price given to order: " + order.getId());
            } else {
                ctx.status(200).result("Price given to order: " + order.getId() + " (but email failed)");
            }
        });

        //TODO
        app.post("submitPaymentForCarport", ctx -> {

            Order order = ctx.attribute("payingForOrder");

            guardAgainstOrderEqualsNull(order);

            boolean mailOK = handlePaymentCarportEmail(order);

            if (mailOK) {
                ctx.status(200).result("Order paid: " + order.getId());
            } else {
                ctx.status(200).result("Order paid: " + order.getId() + " (but email failed)");
            }
        });
    }

    private static Order handleCreateOrder(Context ctx) {

        Order order;

        String firstname = ctx.formParam("firstname");
        String lastname = ctx.formParam("lastname");
        String address = ctx.formParam("address");
        int postalCode = Integer.parseInt(Objects.requireNonNull(ctx.formParam("postalcode")));
        String email = ctx.formParam("email");

        String orderStatus = "PENDING";

        int widthMM = Integer.parseInt(Objects.requireNonNull(ctx.formParam("carportWidth")));
        int heightMM = 2200;
        int lengthMM = Integer.parseInt(Objects.requireNonNull(ctx.formParam("carportLength")));

        List<Material> materials = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        boolean hasShed = ctx.formParam("hasShed") != null;

        try {
            RoofType roofType = OrderMapper.getRoofTypeBySlopeDegrees(Integer.parseInt(Objects.requireNonNull(ctx.formParam("roofSlopeDeg"))));
            CustomerMapper.registerCustomer(email, firstname, lastname, address, postalCode);

            if (!hasShed) {
                order = new Order(email, orderStatus, roofType, widthMM, heightMM, lengthMM, materials, null, totalCost);
            } else {
                int shedWidthMM = Integer.parseInt(Objects.requireNonNull(ctx.formParam("shedWidth")));
                int shedLengthMM = Integer.parseInt(Objects.requireNonNull(ctx.formParam("shedLength")));
                order = new OrderWithShed(email, orderStatus, roofType, widthMM, heightMM, lengthMM, materials, null, totalCost, new Shed(shedWidthMM, shedLengthMM));
            }

            CarportCalculatorService.calculate(order);

            int orderID = OrderMapper.createOrder(order);
            order.setId(orderID);

            ctx.attribute("currentOrder", order);

            return order;

        } catch (NullPointerException | NumberFormatException e) {
            ctx.status(400).result("Invalid form input (missing field or not a number).");
            return null;

        } catch (DatabaseException e) {
            ctx.status(500).result(e.getMessage());
            return null;
        }
    }

    //TODO svgMarkup waiting for Carlus
    private static boolean handleRequestCarportEmail(Order order) {

        CarportMailService carportMailService = new CarportMailService(new MailSender());

        try {
            carportMailService.sendOrderReceived(order, null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //TODO svgMarkup waiting for Carlus
    private static boolean handlePricingCarportEmail(Order order) {

        CarportMailService carportMailService = new CarportMailService(new MailSender());

        try {
            carportMailService.sendPriceGiven(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //TODO svgMarkup waiting for Carlus
    private static boolean handlePaymentCarportEmail(Order order) {

        CarportMailService carportMailService = new CarportMailService(new MailSender());

        try {
            carportMailService.sendOrderPaid(order, null);
            return true;
        } catch (Exception e) {
            return false;
        }

    }
}
