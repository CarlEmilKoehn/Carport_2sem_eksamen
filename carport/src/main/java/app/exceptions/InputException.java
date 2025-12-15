package app.exceptions;

public class InputException extends ApplicationException {

    public InputException(String userMessage, String systemMessage) {
        super(userMessage, systemMessage);
    }
}
