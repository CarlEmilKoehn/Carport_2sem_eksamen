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

    public static void addRoutes(Javalin app) {

        app.post("/submitRequestForCarport", ctx -> {
            Order order = handleCreateOrder(ctx);
            guardAgainstOrderEqualsNull(order);

            boolean mailOK = handleRequestCarportEmail(order);

            if (mailOK) {
                ctx.sessionAttribute("flashSuccess", "Ordre oprettet og bekræftelse sendt.");
            } else {
                ctx.sessionAttribute("flashError", "Ordre oprettet, men mail kunne ikke sendes.");
            }

            ctx.redirect("/");
        });

        app.post("/order/{orderId}/paid", ctx -> {
            int orderId = parseOrderId(ctx);

            try {
                OrderMapper.changeOrderStatus(orderId, "ACCEPTED");

                Order order = OrderMapper.getOrderByOrderId(orderId);
                guardAgainstOrderEqualsNull(order);

                if (order.getMaterials() == null) {
                    order.setMaterials(new ArrayList<>());
                }

                boolean mailOK = handlePaymentCarportEmail(order);

                if (mailOK) {
                    ctx.sessionAttribute("flashSuccess", "Betaling registreret og kvittering sendt.");
                } else {
                    ctx.sessionAttribute("flashError", "Betaling registreret, men kvittering kunne ikke sendes.");
                }

                ctx.redirect("/");

            } catch (DatabaseException e) {
                ctx.sessionAttribute("flashError", "DB-fejl: " + e.getMessage());
                ctx.redirect("/");
            }
        });
    }

    private static int parseOrderId(Context ctx) {
        try {
            return Integer.parseInt(ctx.pathParam("orderId"));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ugyldigt orderId i URL");
        }
    }

    private static void guardAgainstOrderEqualsNull(Order order) {
        if (order == null) {
            throw new IllegalStateException("Order is null");
        }
    }

    private static Order handleCreateOrder(Context ctx) {

        try {
            // Kunde-data
            String firstname = Objects.requireNonNull(ctx.formParam("firstname"));
            String lastname  = Objects.requireNonNull(ctx.formParam("lastname"));
            String address   = Objects.requireNonNull(ctx.formParam("address"));
            int postalCode   = Integer.parseInt(Objects.requireNonNull(ctx.formParam("postalcode")));
            String email     = Objects.requireNonNull(ctx.formParam("email"));

            String orderStatus = "PENDING";
            int widthMM  = Integer.parseInt(Objects.requireNonNull(ctx.formParam("carportWidth")));
            int heightMM = 2200;
            int lengthMM = Integer.parseInt(Objects.requireNonNull(ctx.formParam("carportLength")));

            boolean hasShed = ctx.formParam("hasShed") != null;

            RoofType roofType = OrderMapper.getRoofTypeBySlopeDegrees(
                    Integer.parseInt(Objects.requireNonNull(ctx.formParam("roofSlopeDeg")))
            );

            CustomerMapper.registerCustomer(email, firstname, lastname, address, postalCode);

            List<Material> materials = new ArrayList<>();
            BigDecimal totalCost = BigDecimal.ZERO;

            Order order;
            if (!hasShed) {
                order = new Order(email, orderStatus, roofType, widthMM, heightMM, lengthMM, materials, null, totalCost);
            } else {
                int shedWidthMM  = Integer.parseInt(Objects.requireNonNull(ctx.formParam("shedWidth")));
                int shedLengthMM = Integer.parseInt(Objects.requireNonNull(ctx.formParam("shedLength")));
                order = new OrderWithShed(
                        email, orderStatus, roofType, widthMM, heightMM, lengthMM,
                        materials, null, totalCost,
                        new Shed(shedWidthMM, shedLengthMM)
                );
            }

            CarportCalculatorService.calculate(order);

            int orderID = OrderMapper.createOrder(order);
            order.setId(orderID);

            ctx.sessionAttribute("currentOrderId", orderID);

            return order;

        } catch (NumberFormatException | NullPointerException e) {
            ctx.status(400).result("Invalid form input (missing field or not a number).");
            return null;

        } catch (DatabaseException e) {
            ctx.status(500).result("DB-fejl: " + e.getMessage());
            return null;
        }
    }

    // TODO: svgMarkup når I har det. Indtil da: send uden vedhæftning.
    private static boolean handleRequestCarportEmail(Order order) {
        CarportMailService mailService = new CarportMailService(new MailSender());

        try {
            mailService.sendOrderReceived(order, "");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // TODO: svgMarkup når I har det. Indtil da: send uden vedhæftning.
    private static boolean handlePaymentCarportEmail(Order order) {
        CarportMailService mailService = new CarportMailService(new MailSender());
        try {
            mailService.sendOrderPaid(order, ""); // tom svg string i stedet for null
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
