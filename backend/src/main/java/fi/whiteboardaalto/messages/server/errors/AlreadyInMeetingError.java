package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AlreadyInMeetingError extends Error {
    @JsonCreator
    public AlreadyInMeetingError(@JsonProperty("messageId") int messageId) {
        super(messageId, 209, "You are already in a meeting.");
    }
}
