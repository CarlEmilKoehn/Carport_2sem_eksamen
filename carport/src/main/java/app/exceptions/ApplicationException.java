package app.exceptions;

import lombok.Getter;

@Getter
public abstract class ApplicationException extends Exception {

    private final String userMessage;

    protected ApplicationException(String userMessage, String systemMessage) {
        super(systemMessage);
        this.userMessage = userMessage;
    }
}
