package fi.whiteboardaalto.messages.server.ack.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectCreated extends ObjectAck {
    @JsonCreator
    public ObjectCreated(@JsonProperty("messageId") int messageId, @JsonProperty("checksum") String checksum) {
        super(messageId, 101, checksum);
    }
}
