package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BusyObjectError extends Error {
    @JsonCreator
    public BusyObjectError(@JsonProperty("messageId") int messageId) {
        super(messageId, 431, "Object is already selected by another user.");
    }
}
