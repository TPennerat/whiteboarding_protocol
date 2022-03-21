package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserNotInMeetingError extends Error {
    @JsonCreator
    public UserNotInMeetingError(@JsonProperty("messageId") int messageId) {
        super(messageId, 413, "You can't leave this meeting: you are not in it.");
    }
}
