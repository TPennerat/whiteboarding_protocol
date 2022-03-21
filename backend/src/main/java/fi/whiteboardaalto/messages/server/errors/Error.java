package fi.whiteboardaalto.messages.server.errors;

import fi.whiteboardaalto.messages.server.Answer;

public class Error extends Answer {
    private String message;

    public Error(int messageId, int code, String message) {
        super(messageId, code);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
