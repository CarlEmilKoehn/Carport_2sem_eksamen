package app.controllers;

import app.entities.Admin;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;

public class AdminController {

    public static void addRoutes(Javalin app) {

        app.before("/admin/dashboard", AdminController::handleGuard);
        app.before("/admin/ordre/*", AdminController::handleGuard);
        app.before("/admin/logout", AdminController::handleGuard);

        app.get("/admin/login", ctx -> ctx.render("admin_login"));
        app.post("/admin/login", AdminController::handleLogin);

        app.get("/admin/dashboard", ctx -> {
            String status = ctx.queryParam("status");

            List<Order> orders = (status == null || status.isBlank())
                    ? OrderMapper.getAllOrders()
                    : OrderMapper.getOrdersByStatus(status);

            pullFlash(ctx);

            ctx.attribute("allOrders", orders);
            ctx.attribute("selectedStatus", status == null ? "ALL" : status.toUpperCase());
            ctx.render("admin_dashboard");
        });

        app.get("/admin/ordre/{orderId}", ctx -> {
            try {
                int orderId = Integer.parseInt(ctx.pathParam("orderId"));
                Order order = OrderMapper.getOrderByOrderId(orderId);

                if (order == null) {
                    ctx.sessionAttribute("flashError", "Ordren blev ikke fundet");
                    ctx.redirect("/admin/dashboard");
                    return;
                }

                BigDecimal basePrice = order.getTotalPrice() != null ? order.getTotalPrice() : BigDecimal.ZERO;

                BigDecimal suggestedPrice = basePrice
                        .multiply(BigDecimal.valueOf(1.40))
                        .setScale(2, RoundingMode.HALF_UP);

                pullFlash(ctx);

                ctx.attribute("order", order);
                ctx.attribute("basePrice", basePrice);
                ctx.attribute("suggestedPrice", suggestedPrice);

                ctx.render("showOrder");

            } catch (NumberFormatException e) {
                ctx.sessionAttribute("flashError", "Ugyldigt ordre-id");
                ctx.redirect("/admin/dashboard");
            } catch (DatabaseException e) {
                ctx.sessionAttribute("flashError", "DB-fejl: " + e.getMessage());
                ctx.redirect("/admin/dashboard");
            } catch (Exception e) {

                ctx.sessionAttribute("flashError", "Fejl ved visning af ordre: " + e.getMessage());
                ctx.redirect("/admin/dashboard");
            }
        });

        app.post("/admin/ordre/{orderId}/price", AdminController::setPrice);

        app.get("/admin/logout", AdminController::handleLogout);
    }

    private static void handleGuard(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");
        if (admin == null) {
            ctx.redirect("/admin/login");
            ctx.skipRemainingHandlers();
        }
    }

    private static void handleLogin(Context ctx) {
        String email = ctx.formParam("admin_email");
        String password = ctx.formParam("admin_password");

        try {
            Admin admin = AdminMapper.login(email, password);

            if (admin == null) {
                ctx.attribute("fejl", "Forkert email eller adgangskode");
                ctx.render("admin_login");
                return;
            }

            ctx.sessionAttribute("currentAdmin", admin);
            ctx.redirect("/admin/dashboard");

        } catch (DatabaseException e) {
            ctx.attribute("fejl", e.getMessage());
            ctx.render("admin_login");
        }
    }

    private static void setPrice(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");
        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        try {
            String priceParam = Objects.requireNonNull(ctx.formParam("price"));
            BigDecimal newPrice = new BigDecimal(priceParam).setScale(2, RoundingMode.HALF_UP);

            String comment = ctx.formParam("comment");

            OrderMapper.changeOrderPrice(orderId, newPrice, admin, comment);
            OrderMapper.changeOrderStatus(orderId, "ADJUSTED");

            ctx.sessionAttribute("flashSuccess", "Pris gemt og status sat til ADJUSTED");
            ctx.redirect("/admin/ordre/" + orderId);

        } catch (NumberFormatException e) {
            ctx.sessionAttribute("flashError", "Ugyldig pris");
            ctx.redirect("/admin/ordre/" + orderId);

        } catch (DatabaseException e) {
            ctx.sessionAttribute("flashError", "DB-fejl: " + e.getMessage());
            ctx.redirect("/admin/ordre/" + orderId);
        }
    }

    private static void handleLogout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/admin/login");
    }

    private static void pullFlash(Context ctx) {
        String ok = ctx.sessionAttribute("flashSuccess");
        String err = ctx.sessionAttribute("flashError");

        if (ok != null) {
            ctx.attribute("flashSuccess", ok);
            ctx.sessionAttribute("flashSuccess", null);
        }
        if (err != null) {
            ctx.attribute("flashError", err);
            ctx.sessionAttribute("flashError", null);
        }
    }
}
