package fi.whiteboardaalto.messages.client;

import fi.whiteboardaalto.messages.Message;

public class ActionMessage extends Message {

    private String userId;

    public ActionMessage(int messageId, String userId) {
        super(messageId);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
