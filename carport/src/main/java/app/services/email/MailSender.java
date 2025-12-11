package app.services.email;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;

import java.util.List;
import java.util.Properties;

public class MailSender {


    private final String username;
    private final String password;

    public MailSender() {
        this.username = System.getenv("MAIL_USERNAME");
        this.password = System.getenv("MAIL_PASSWORD");

        if (username == null || password == null) {
            throw new IllegalStateException("MAIL_USERNAME and MAIL_PASSWORD environment variables must be set.");
        }
    }

    public void sendEmail(String to, String subject, String body, List<MimeBodyPart> attachments) throws MessagingException {

        Session session = createSession();

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, to);
        message.setSubject(subject, "UTF-8");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(body, "UTF-8");

        MimeMultipart multipart = new MimeMultipart("mixed");
        multipart.addBodyPart(textPart);

        if (attachments != null) {
            for (MimeBodyPart part : attachments) {
                multipart.addBodyPart(part);
            }
        }

        message.setContent(multipart);
        Transport.send(message);
    }

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        sendEmail(to, subject, body, null);
    }

    private Session createSession() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public MimeBodyPart createAttachment(String filename, String mimeType, byte[] data) throws MessagingException {
        DataSource ds = new ByteArrayDataSource(data, mimeType);
        MimeBodyPart part = new MimeBodyPart();
        part.setDataHandler(new DataHandler(ds));
        part.setFileName(filename);
        return part;
    }
}