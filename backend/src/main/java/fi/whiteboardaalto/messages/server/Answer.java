package fi.whiteboardaalto.messages.server;

import fi.whiteboardaalto.messages.Message;

public class Answer extends Message {
    private int code;

    public Answer(int messageId, int code) {
        super(messageId);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
