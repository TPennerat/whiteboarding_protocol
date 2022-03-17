package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetingAlreadyCreatedError extends Error {
    @JsonCreator
    public MeetingAlreadyCreatedError(@JsonProperty("meetingId") int messageId) {
        super(messageId, 210, "You have already created a meeting.");
    }
}
