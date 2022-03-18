package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectNotOwnedError extends Error {
    @JsonCreator
    public ObjectNotOwnedError(@JsonProperty("messageId") int messageId) {
        super(messageId, 211, "You can't unselect this object: not priorly selected.");
    }
}
