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

        app.post("/carportFlatRoof", OrderController::showSvg);
    }

    private static void showSvg(Context ctx) {
        Locale.setDefault(Locale.US);

        int carportWidth = Integer.parseInt(ctx.formParam("carportWidthMm"));
        int carportLength = Integer.parseInt(ctx.formParam("carportLengthMm"));

        int shedWidth = ctx.formParam("shedWidthMm").isEmpty()
                ? 0
                : Integer.parseInt(ctx.formParam("shedWidthMm"));

        int shedLength = ctx.formParam("shedLengthMm").isEmpty()
                ? 0
                : Integer.parseInt(ctx.formParam("shedLengthMm"));

        CarportSvg svg = new CarportSvg(
                carportWidth,
                carportLength,
                shedWidth,
                shedLength
        );

        ctx.attribute("svg", svg.toString());
        ctx.render("carportFlatRoof.html");
    }
}
