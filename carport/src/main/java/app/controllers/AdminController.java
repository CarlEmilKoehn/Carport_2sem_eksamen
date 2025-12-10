package app.controllers;

import app.entities.Admin;
import app.entities.Order;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.MaterialMapper;
import app.persistence.OrderMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class AdminController {

    public static void addRoutes(Javalin app) {

        app.get("/admin/login", ctx -> ctx.render("admin_login.html"));
        app.post("/admin/login", ctx -> handleLogin(ctx));
        app.get("/admin/dashboard", ctx -> visDashboard(ctx));
        app.get("/admin/logout", ctx -> logUd(ctx));
    }

    private static void handleLogin(Context ctx) {
        String email = ctx.formParam("admin_email");
        String password = ctx.formParam("admin_password");

        try {
            Admin admin = AdminMapper.login(email, password);
            ctx.sessionAttribute("currentAdmin", admin);
            ctx.redirect("/admin/dashboard");

        } catch (DatabaseException e) {
            ctx.attribute("fejl", "Ugyldig email eller kodeord.");
            ctx.render("admin_login.html");
        }
    }

    private static void visDashboard(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");

        if (admin == null) {
            ctx.redirect("/admin/login");
            return;
        }

        try {
            List<Order> allOrders = OrderMapper.getAllOrders();
            ctx.attribute("allOrders", allOrders);
            ctx.render("admin_dashboard.html");

        } catch (DatabaseException e) {
            ctx.attribute("fejl", "Kunne ikke hente alle ordrer");
            ctx.render("admin_dashboard.html");
        }
    }

    private static void logUd(Context ctx) {
        ctx.req().getSession().invalidate();
        ctx.redirect("/admin/login");
    }
}
