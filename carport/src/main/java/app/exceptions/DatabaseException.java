package app.exceptions;

public class DatabaseException extends ApplicationException {

    public DatabaseException(String userMessage, String systemMessage) {
        super(userMessage, systemMessage);
    }
}
