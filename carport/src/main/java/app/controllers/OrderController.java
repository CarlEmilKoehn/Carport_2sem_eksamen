package app.controllers;

import app.entities.Material;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.List;


public class OrderController {

    public static void addRoutes(Javalin app) {
        String svgText= "";
        ctx.attribute("svg", svgText);
        ctx.render("showOrder.html");
    }

}
