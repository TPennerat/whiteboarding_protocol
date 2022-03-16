package fi.whiteboardaalto.messages.server.ack.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectCreated extends ObjectAck {
    private String objectId;
    @JsonCreator
    public ObjectCreated(@JsonProperty("messageId") int messageId, @JsonProperty("ObjectId") String objectId, @JsonProperty("checksum") String checksum) {
        super(messageId, 102, checksum);
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

}
