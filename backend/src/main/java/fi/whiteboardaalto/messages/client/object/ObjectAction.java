package fi.whiteboardaalto.messages.client.object;

import fi.whiteboardaalto.messages.client.ActionMessage;

public class ObjectAction extends ActionMessage {

    private String objectId;

    public ObjectAction(int messageId, String userId, String objectId) {
        super(messageId, userId);
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

}
