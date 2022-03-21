package fi.whiteboardaalto.messages.server.errors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BusyPseudoError extends Error {
    @JsonCreator
    public BusyPseudoError(@JsonProperty("messageId") int messageId) {
        super(messageId, 412, "Pseudo already taken.");
    }
}
