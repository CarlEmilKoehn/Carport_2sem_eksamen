package app.controllers;

import app.exceptions.DatabaseException;
import io.javalin.Javalin;
import io.javalin.http.Context;


public class CustomerController {

    public static void addRoutes(Javalin app) {

    }

    //TODO: Håndter oprettelse af customer enten her eller i ordercontroller
    private static void handleNewCustomer(Context ctx) throws DatabaseException {

        String email = ctx.formParam("email");
        String firstName = ctx.formParam("firstName");
        String lastName = ctx.formParam("lastName");
        String address = ctx.formParam("address");
        int postalCode = Integer.parseInt(ctx.formParam("postalCode"));

    }

}
