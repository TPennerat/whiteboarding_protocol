package fi.whiteboardaalto.messages.client.action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fi.whiteboardaalto.messages.Message;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionMessage extends Message {

    private String userId;

    public ActionMessage(int messageId, String userId) {
        this.messageId = messageId;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}
