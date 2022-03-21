package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ChangeNotAllowedError extends Error {
    @JsonCreator
    public ChangeNotAllowedError(@JsonProperty("messageId") int messageId) {
        super(messageId, 436, "Change not allowed for this type of object.");
    }
}
