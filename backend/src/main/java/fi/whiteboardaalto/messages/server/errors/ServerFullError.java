package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ServerFullError extends Error {
    @JsonCreator
    public ServerFullError(@JsonProperty("messageId") int messageId) {
        super(messageId, 201, "The chosen coordinates are already in use.");
    }
}
