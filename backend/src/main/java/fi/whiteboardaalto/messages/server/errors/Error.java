package fi.whiteboardaalto.messages.server.errors;

import fi.whiteboardaalto.messages.server.Answer;
import fi.whiteboardaalto.messages.server.AnswerType;

public class Error extends Answer {
    private String message;

    public Error(int messageId, AnswerType answerType, int code, String message) {
        super(messageId, answerType, code);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
