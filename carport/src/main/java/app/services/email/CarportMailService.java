package app.services.email;

import app.entities.Order;

public class CarportMailService {

    private final MailSender mailSender;
    private final EmailTemplateBuilder templates;

    public CarportMailService(MailSender mailSender) {
        this.mailSender = mailSender;
        this.templates = new EmailTemplateBuilder();
    }

    public void sendOrderReceived(Order order) throws Exception {
        String body = templates.buildOrderReceivedText(order);

        mailSender.sendEmail(
                order.getEmail(),
                "Tak for din ordre #" + order.getId(),
                body
        );
    }

    public void sendPriceGiven(Order order) throws Exception {
        String body = templates.buildPriceGivenText(order);

        mailSender.sendEmail(
                order.getEmail(),
                "Dit tilbud er klar – ordre #" + order.getId(),
                body
        );
    }
}