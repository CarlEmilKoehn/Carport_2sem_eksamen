package app.controllers;

import app.entities.Admin;
import app.entities.Material;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.MaterialMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.List;

public class OrderController {

    public static void addRoutes(Javalin app) {
        app.get("/admin/ordre/{orderId}", ctx -> visOrdre(ctx));
        app.post("/admin/ordre/{orderId}", ctx -> handleOrdreUpdate(ctx));
    }

    private static void visOrdre(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");

        if (admin == null) {
            ctx.redirect("/admin/login");
            return;
        }

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        try {
            Order order = OrderMapper.getOrderByOrderId(orderId);
            List<Material> materials = MaterialMapper.getAllMaterialsFromOrder(orderId);

            BigDecimal calculatedPrice = materials.stream()
                    .map(Material::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String existingNote = null;
            BigDecimal adjustedPrice = null;
            if (!order.getComments().isEmpty()) {
                existingNote = order.getComments().get(order.getComments().size() - 1).getNote();
                adjustedPrice = order.getTotalPrice();
            }

            ctx.attribute("order", order);
            ctx.attribute("orderMaterials", materials);
            ctx.attribute("calculatedPrice", calculatedPrice);
            ctx.attribute("existingNote", existingNote);
            ctx.attribute("adjustedPrice", adjustedPrice);
            ctx.render("admin_ordre.html");

        } catch (DatabaseException e) {
            ctx.attribute("fejl", "Kunne ikke hente ordre");
            ctx.redirect("/admin/dashboard");
        }
    }

    private static void handleOrdreUpdate(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");

        if (admin == null) {
            ctx.redirect("/admin/login");
            return;
        }

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));
        String action = ctx.formParam("action");
        String adjustedPriceStr = ctx.formParam("adjustedPrice");
        String comment = ctx.formParam("adminNote");

        try {
            if (adjustedPriceStr != null && !adjustedPriceStr.isBlank()) {
                BigDecimal newPrice = new BigDecimal(adjustedPriceStr);
                OrderMapper.changeOrderPrice(orderId, newPrice, admin, comment);
            } else if (comment != null && !comment.isBlank()) {
                Order order = OrderMapper.getOrderByOrderId(orderId);
                OrderMapper.changeOrderPrice(orderId, order.getTotalPrice(), admin, comment);
            }

            if ("accept".equals(action)) {
                OrderMapper.changeOrderStatus(orderId, "accepted");
            } else if ("reject".equals(action)) {
                OrderMapper.changeOrderStatus(orderId, "rejected");
            }

            ctx.redirect("/admin/dashboard");

        } catch (NumberFormatException e) {
            ctx.attribute("fejl", "Ugyldig pris");
            ctx.redirect("/admin/ordre/" + orderId);
        } catch (DatabaseException e) {
            ctx.attribute("fejl", "Kunne ikke opdatere ordre");
            ctx.redirect("/admin/ordre/" + orderId);
        }
    }
}