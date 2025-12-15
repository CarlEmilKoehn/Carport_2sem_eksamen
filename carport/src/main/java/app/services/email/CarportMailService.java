package app.services.email;

import app.entities.Material;
import app.entities.Order;
import jakarta.mail.internet.MimeBodyPart;

import java.nio.charset.StandardCharsets;
import java.util.List;

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

    public void sendOrderPaid(Order order) throws Exception {
        String body = templates.buildOrderPaidText(order);

        StringBuilder materialListBuilder = new StringBuilder();
        materialListBuilder.append("Styklisten for din ordre:\n\n");

        for (Material m : order.getMaterials()) {
            materialListBuilder.append(m.toString())
                               .append(System.lineSeparator());
        }

        MimeBodyPart materialListAttachment = mailSender.createAttachment(
                "stykliste.txt",
                "text/plain",
                materialListBuilder.toString().getBytes(StandardCharsets.UTF_8)
        );

        mailSender.sendEmail(
                order.getEmail(),
                "Kvittering for dit køb - ordre #" + order.getId(),
                body,
                List.of(materialListAttachment)
        );
    }
}