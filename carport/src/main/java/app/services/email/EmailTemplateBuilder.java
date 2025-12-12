package app.services.email;

import app.entities.Order;
import app.entities.OrderWithShed;

public class EmailTemplateBuilder {

    public String buildOrderReceivedText(Order order) {

        if (order instanceof OrderWithShed ows && ows.getRoofType().getDegrees() != 0) {
            return """
                Tak for din ordre med skur!
                
                Ordre-ID: %d
                Bredde: %d mm
                Højde: %d mm
                Længde: %d mm
                Tagtype: %s %s°
                
                Skurets bredde: %d mm
                Skurets længde: %d mm
                
                En sælger vender tilbage inden for 1–3 hverdage.
                
                Venlig hilsen
                FOG
                """.formatted(
                    ows.getId(),
                    ows.getWidthMM(),
                    ows.getHeightMM(),
                    ows.getLengthMM(),
                    ows.getRoofType().getName(),
                    ows.getRoofType().getDegrees(),
                    ows.getShed().getWidthMM(),
                    ows.getShed().getLengthMM()
            );

        } else if (order instanceof OrderWithShed ows) {

            return """
                Tak for din ordre med skur!
                
                Ordre-ID: %d
                Bredde: %d mm
                Højde: %d mm
                Længde: %d mm
                Tagtype: %s
                
                Skurets bredde: %d mm
                Skurets længde: %d mm
                
                En sælger vender tilbage inden for 1–3 hverdage.
                
                Venlig hilsen
                FOG
                """.formatted(
                    ows.getId(),
                    ows.getWidthMM(),
                    ows.getHeightMM(),
                    ows.getLengthMM(),
                    ows.getRoofType().getName(),
                    ows.getShed().getWidthMM(),
                    ows.getShed().getLengthMM()
            );

        } else if (order.getRoofType().getDegrees() != 0) {

            return """
                Tak for din ordre!
                
                Ordre-ID: %d
                Bredde: %d mm
                Højde: %d mm
                Længde: %d mm
                Tagtype: %s %s°
                
                En sælger vender tilbage inden for 1–3 hverdage.
                
                Venlig hilsen
                FOG
                """.formatted(
                    order.getId(),
                    order.getWidthMM(),
                    order.getHeightMM(),
                    order.getLengthMM(),
                    order.getRoofType().getName(),
                    order.getRoofType().getDegrees()
            );

        } else {

            return """
                Tak for din ordre!
                
                Ordre-ID: %d
                Bredde: %d mm
                Højde: %d mm
                Længde: %d mm
                Tagtype: %s
                
                En sælger vender tilbage inden for 1–3 hverdage.
                
                Venlig hilsen
                FOG
                """.formatted(
                    order.getId(),
                    order.getWidthMM(),
                    order.getHeightMM(),
                    order.getLengthMM(),
                    order.getRoofType().getName()
            );
        }
    }

    //TODO insert which domain to redirect to.
    public String buildPriceGivenText(Order order) {

        return """
                    Dit tilbud er klar!
                    
                    Ordre-ID: %d
                    Pris: %s kr.
                    
                    website link!
                    
                    Når du accepterer tilbuddet, sender vi en købsbekræftelse.
                    """.formatted(
                        order.getId(),
                        order.getTotalPrice());
    }

    public String buildOrderPaidText(Order order) {

        return """
                Tak for dit køb!
                
                Ordre-ID: %d
                Din betaling er modtaget.
                
                Vedhæftet finder du styklisten og tegningen.
                """.formatted(
                    order.getId());
    }
}
