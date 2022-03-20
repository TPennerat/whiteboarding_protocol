package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NonExistentMeetingError extends Error {
    @JsonCreator
    public NonExistentMeetingError(@JsonProperty("messageId") int messageId) {
        super(messageId, 403,"Meeting doesn't exist.");
    }
}
