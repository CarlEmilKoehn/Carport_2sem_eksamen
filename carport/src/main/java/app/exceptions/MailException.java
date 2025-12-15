package app.exceptions;

public class MailException extends ApplicationException {

    public MailException(String userMessage, String systemMessage) {
        super(userMessage, systemMessage);
    }
}
