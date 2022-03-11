package fi.whiteboardaalto.messages.client.object;

import fi.whiteboardaalto.messages.client.ActionMessage;

public class ObjectAction extends ActionMessage {

    private int objectId;

    public ObjectAction(int messageId, String userId, int objectId) {
        super(messageId, userId);
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

}
