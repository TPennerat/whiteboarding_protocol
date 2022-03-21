package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectNotOwnedError extends Error {
    @JsonCreator
    public ObjectNotOwnedError(@JsonProperty("messageId") int messageId) {
        super(messageId, 434, "The object is selected but not by you.");
    }
}
