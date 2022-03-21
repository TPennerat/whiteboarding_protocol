package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectNotFoundError extends Error {
    @JsonCreator
    public ObjectNotFoundError(@JsonProperty("messageId") int messageId) {
        super(messageId, 433, "Object not found.");
    }
}
