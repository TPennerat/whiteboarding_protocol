package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.messages.server.AnswerType;


public class BusyCoordinatesError extends Error {
    @JsonCreator
    public BusyCoordinatesError(@JsonProperty("messageId") int messageId) {
        super(messageId, AnswerType.BUSY_COORDINATES_ERROR, 201, "The chosen coordinates are already in use.");
    }
}