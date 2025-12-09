package app.controllers;

import app.entities.Admin;
import app.exceptions.DatabaseException;
import app.persistence.AdminMapper;
import app.persistence.ConnectionPool;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class AdminController {

    public static void addRoutes(Javalin app) {
        app.get("/admin/login", ctx -> ctx.render("admin_login.html"));
        app.post("/admin/login", ctx -> handleLogin(ctx));
        app.get("/admin/dashboard", ctx -> showDashboard(ctx));
        app.get("/admin/ordre/{ordreId}", ctx -> showDashboard(ctx));
        app.post("/admin/ordre/{ordreId}/pris", ctx -> showDashboard(ctx));
        app.get("/admin/logout", ctx -> logUd(ctx));
    }

    private static void handleLogin(Context ctx) {
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        String email = ctx.formParam("admin_email");
        String password = ctx.formParam("admin_password");

        try {
            Admin admin = AdminMapper.login(email, password, connectionPool);
            ctx.sessionAttribute("currentAdmin", admin);
            ctx.redirect("/admin/dashboard");

        } catch (DatabaseException e) {

            ctx.attribute("fejl", "Ugyldig email eller kodeord.");
            ctx.render("admin_login.html");
        }
    }

    //Oliver: når order mapper hentAlleOrdrer er implementeret skal ordrerne hentes og sendes til templaten (ctx.attribute)

    private static void showDashboard(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");

        if (admin == null) {
            ctx.redirect("/admin/login");
            return;
        }
        ConnectionPool connectionPool = ConnectionPool.getInstance();

        //Oliver: Vi skal hente alle ordrer fra ordremapper - list med ordre ordremapper henter så alle ordrer (connectionPool)

        ctx.attribute("admin", admin);
        ctx.render("admin_dashboard.html");
    }


    // Oliver: Vis ordre og redigeringsmulighed (pris og kommentarer)

    // Oliver: når order mapper hentOrdre er implementeret skal ordrerne hentes og sendes til templaten (ctx.attribute)

    private static void visOrdre(Context ctx) {

     Admin admin = ctx.sessionAttribute("currentAdmin");

        if (admin == null) {
            ctx.redirect("/admin/login");
            return;
        }

        int ordreId = Integer.parseInt(ctx.pathParam("ordreId"));

        // Oliver: Vi skal lige hente ordre detaljerne fra ordremapper, ctx attribute ordre ordre

        ctx.attribute("admin", admin);
        ctx.attribute("ordreId", ordreId);
        ctx.render("admin_ordre.html");
    }

    // Oliver: kald order mapper opdater pris og opret note/kommentar så de kan gemmes i vores db
    private static void setPris(Context ctx) {
        Admin admin = ctx.sessionAttribute("currentAdmin");

        if (admin == null) {
            ctx.redirect("/admin/login");
            return;
        }

        ConnectionPool connectionPool = ConnectionPool.getInstance();
        int ordreId = Integer.parseInt(ctx.pathParam("ordreId"));
        String samletPris = ctx.formParam("pris");
        String kommentar = ctx.formParam("comment");

        try {
            double pris = Double.parseDouble(samletPris);

            // Opdater ordre pris (ordreId, pris og connectionPool)
            // Change mapperen skal oprette note/kommentar (ordreId, note, admin email, connectionPool)

            ctx.attribute("succes", "Prisen er blevet nedsat. Der sendes en automatisk ordrebekræftigelse på mail til kunden.");
            ctx.redirect("/admin/dashboard");
        } catch (NumberFormatException e) {
            ctx.attribute("fejl", "Ugyldig pris");
            ctx.redirect("/admin/ordre/" + ordreId);
        }
    }

    private static void logUd(Context ctx) {
        ctx.sessionAttribute("currentAdmin", null);
        ctx.redirect("/admin/login");
    }
}
