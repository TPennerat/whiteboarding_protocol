package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BusyCoordinatesError extends Error {
    @JsonCreator
    public BusyCoordinatesError(@JsonProperty("messageId") int messageId) {
        super(messageId, 202, "The chosen coordinates are already in use.");
    }
}