package fi.whiteboardaalto.messages.server.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.messages.server.Answer;

public class DeleteBroadcast extends Answer {
    private String objectId;

    @JsonCreator
    public DeleteBroadcast(@JsonProperty("messageId") int messageId, @JsonProperty("objectId") String objectId) {
        super(messageId, 403);
        this.objectId = objectId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }
}
