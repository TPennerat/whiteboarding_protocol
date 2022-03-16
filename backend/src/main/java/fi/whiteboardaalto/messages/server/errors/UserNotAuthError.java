package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserNotAuthError extends Error {
    @JsonCreator
    public UserNotAuthError(@JsonProperty("messageId") int messageId) {
        super(messageId, 203, "User not authenticated.");
    }
}
