package fi.whiteboardaalto.messages.client.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeleteObject extends ObjectAction {
    @JsonCreator
    public DeleteObject(@JsonProperty("messageId") int messageId, @JsonProperty("userId") String userId, @JsonProperty("objectId") String objectId) {
        super(messageId, userId, objectId);
    }
}
