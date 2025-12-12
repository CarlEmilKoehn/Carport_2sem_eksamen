package app.controllers;

import app.entities.CarportSvg;
import app.entities.Material;
import app.entities.Svg;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;


public class OrderController {

    public static void addRoutes(Javalin app) {
        app.get("/showOrder", ctx -> showSvg(ctx));

    }

    private static void showSvg(Context ctx) {
        Locale.setDefault(new Locale("US"));
        //TODO: ændre navne til de passer html
        int width = Integer.parseInt(ctx.formParam("bredde"));
        int length = Integer.parseInt(ctx.formParam("længde"));
        CarportSvg svg = new CarportSvg(780, 600, 0, 0);

        ctx.attribute("svg", svg.toString());
        ctx.render("showOrder.html");
    }
}
