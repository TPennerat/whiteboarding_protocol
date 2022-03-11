package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.messages.server.AnswerType;

public class ServerFullError extends Error {
    @JsonCreator
    public ServerFullError(@JsonProperty("messageId") int messageId) {
        super(messageId, AnswerType.SERVER_FULL_ERROR, 201, "The chosen coordinates are already in use.");
    }
}
