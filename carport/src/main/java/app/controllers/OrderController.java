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

public class OrderController {

    public static void addRoutes(Javalin app) {

        app.get("/customMadeCarport", ctx -> ctx.render("customMadeCarport.html"));
        app.get("/carportFlatRoof", ctx -> ctx.render("carportFlatRoof.html"));
        app.get("/carportSlopedRoof", ctx -> ctx.render("carportSlopedRoof.html"));

        app.post("/createOrder", ctx -> {
            try {
                Order order = createOrderFromForm(ctx);

                int orderId = OrderMapper.createOrder(order);
                order.setId(orderId);

                boolean mailOK = sendOrderReceivedEmail(order);

                ctx.sessionAttribute(
                        mailOK ? "flashSuccess" : "flashError",
                        mailOK
                                ? "Ordre oprettet og bekræftelse sendt."
                                : "Ordre oprettet, men mail kunne ikke sendes."
                );

                ctx.sessionAttribute("currentOrderId", orderId);
                ctx.redirect("/");

            } catch (IllegalArgumentException | DatabaseException e) {
                ctx.sessionAttribute("flashError", e.getMessage());
                ctx.redirect("/");

            } catch (Exception e) {
                e.printStackTrace();
                ctx.sessionAttribute("flashError", "Der skete en uventet fejl. Prøv igen.");
                ctx.redirect("/");
            }
        });

        app.post("/order/{orderId}/paid", ctx -> {
            try {
                int orderId = parseOrderId(ctx);

                Order order = OrderMapper.getOrderByOrderId(orderId);
                if (order == null) {
                    throw new IllegalArgumentException("Ordren findes ikke.");
                }

                OrderMapper.changeOrderStatus(orderId, "ACCEPTED");

                if (order.getMaterials() == null) {
                    order.setMaterials(new ArrayList<>());
                }

                boolean mailOK = sendOrderPaidEmail(order);

                ctx.sessionAttribute(
                        mailOK ? "flashSuccess" : "flashError",
                        mailOK
                                ? "Betaling registreret og kvittering sendt."
                                : "Betaling registreret, men kvittering kunne ikke sendes."
                );

                ctx.redirect("/");

            } catch (IllegalArgumentException | DatabaseException e) {
                ctx.sessionAttribute("flashError", e.getMessage());
                ctx.redirect("/");

            } catch (Exception e) {
                e.printStackTrace();
                ctx.sessionAttribute("flashError", "Der skete en uventet fejl. Prøv igen.");
                ctx.redirect("/");
            }
        });
    }

    private static int parseOrderId(Context ctx) {
        try {
            return Integer.parseInt(ctx.pathParam("orderId"));
        } catch (Exception e) {
            throw new IllegalArgumentException("Ugyldigt orderId i URL.");
        }
    }

    private static Order createOrderFromForm(Context ctx) throws DatabaseException {

        String firstname = require(ctx.formParam("firstname"), "Firstname mangler.");
        String lastname  = require(ctx.formParam("lastname"), "Lastname mangler.");
        String address   = require(ctx.formParam("address"), "Adresse mangler.");
        int postalCode   = parseInt(require(ctx.formParam("postalcode"), "Postnr mangler."), "Ugyldigt postnr.");
        String email     = require(ctx.formParam("email"), "Email mangler.");

        int widthMM  = parseInt(require(ctx.formParam("carportWidthMm"), "Bredde mangler."), "Ugyldig bredde.");
        int lengthMM = parseInt(require(ctx.formParam("carportLengthMm"), "Længde mangler."), "Ugyldig længde.");

        int slopeDeg = parseInt(require(ctx.formParam("roofSlopeDeg"), "Taghældning mangler."), "Ugyldig taghældning.");
        RoofType roofType = OrderMapper.getRoofTypeBySlopeDegrees(slopeDeg);

        boolean hasShed = ctx.formParam("shedWidthMm") != null && ctx.formParam("shedLengthMm") != null;

        CustomerMapper.registerCustomer(email, firstname, lastname, address, postalCode);

        List<Material> materials = new ArrayList<>();
        BigDecimal totalCost = BigDecimal.ZERO;

        Order order;
        if (!hasShed) {
            order = new Order(email, "PENDING", roofType, widthMM, 2200, lengthMM, materials, null, totalCost);
        } else {
            int shedWidthMM  = parseInt(require(ctx.formParam("shedWidthMm"), "Skur bredde mangler."), "Ugyldig skur bredde.");
            int shedLengthMM = parseInt(require(ctx.formParam("shedLengthMm"), "Skur længde mangler."), "Ugyldig skur længde.");

            order = new OrderWithShed(
                    email, "PENDING", roofType, widthMM, 2200, lengthMM,
                    materials, null, totalCost,
                    new Shed(shedWidthMM, shedLengthMM)
            );
        }

        CarportCalculatorService.calculate(order);
        return order;
    }

    private static String require(String value, String messageIfMissing) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(messageIfMissing);
        return value;
    }

    private static int parseInt(String value, String messageIfBad) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(messageIfBad);
        }
    }

    private static boolean sendOrderReceivedEmail(Order order) {
        CarportMailService mailService = new CarportMailService(new MailSender());
        try {
            mailService.sendOrderReceived(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean sendOrderPaidEmail(Order order) {
        CarportMailService mailService = new CarportMailService(new MailSender());
        try {
            mailService.sendOrderPaid(order);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
