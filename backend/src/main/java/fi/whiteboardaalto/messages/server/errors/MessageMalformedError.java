package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MessageMalformedError extends Error {
    @JsonCreator
    public MessageMalformedError(@JsonProperty("messageId") int messageId) {
        super(messageId, 207, "Message malformed.");
    }
}
