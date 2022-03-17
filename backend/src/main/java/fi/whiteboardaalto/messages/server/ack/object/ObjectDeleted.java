package fi.whiteboardaalto.messages.server.ack.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectDeleted extends ObjectAck {
    @JsonCreator
    public ObjectDeleted(@JsonProperty("messageId") int messageId, @JsonProperty("checksum") String checksum) {
        super(messageId, 103, checksum);
    }
}
