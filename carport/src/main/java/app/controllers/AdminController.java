package app.controllers;

import app.entities.Admin;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class AdminController {

    public static void addRoutes(Javalin app) {

        app.before("/admin/dashboard", AdminController::handleGuard);
        app.before("/admin/ordre/*", AdminController::handleGuard);
        app.before("/admin/logout", AdminController::handleGuard);

        app.get("/admin/login", ctx -> ctx.render("admin_login"));
        app.post("/admin/login", ctx -> handleLogin(ctx));

        app.get("/admin/dashboard", ctx -> {
            String status = ctx.queryParam("status");

            List<Order> orders = (status == null || status.isBlank())
                    ? OrderMapper.getAllOrders()
                    : OrderMapper.getOrdersByStatus(status);


            ctx.attribute("allOrders", orders);
            ctx.attribute("selectedStatus", status == null ? "all" : status.toUpperCase());
            ctx.render("admin_dashboard");
        });

        app.get("/admin/ordre/{orderId}", ctx -> {

            int orderId = Integer.parseInt(ctx.pathParam("orderId"));

            Order order = OrderMapper.getOrderByOrderId(orderId);

            if (order == null) {
                ctx.attribute("fejl", "Ordren blev ikke fundet");
                ctx.redirect("/admin/dashboard");
                return;
            }

            Admin admin = ctx.sessionAttribute("currentAdmin");

            ctx.attribute("admin", admin);
            ctx.attribute("order", order);

            ctx.render("admin_ordre");
        });

        app.post("/admin/ordre/{orderId}/price", AdminController::setPrice);


        app.get("/admin/logout", ctx -> handleLogout(ctx));
    }

    private static void handleGuard(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");

        if (admin == null) {
            ctx.redirect("/admin/login");
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
            ctx.attribute("fejl", e);
            ctx.render("admin_login");
        }
    }

    private static void setPrice(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");

        int orderId = Integer.parseInt(ctx.pathParam("orderId"));

        BigDecimal totalPrice = new BigDecimal(Objects.requireNonNull(ctx.formParam("price")));

        String comment = ctx.formParam("comment");

        try {

            OrderMapper.changeOrderPrice(orderId, totalPrice, admin, comment);

            ctx.attribute("succes", "Prisen er blevet nedsat. Der sendes en automatisk ordrebekræftigelse på mail til kunden.");
            ctx.redirect("/admin/dashboard");

        } catch (NumberFormatException | DatabaseException e) {
            ctx.attribute("fejl", "Ugyldig pris");
            ctx.redirect("/admin/ordre/" + orderId);
        }
    }

    private static void handleLogout(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/admin/login");
    }
}
