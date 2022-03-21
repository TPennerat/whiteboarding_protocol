package fi.whiteboardaalto.messages.server.ack.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectUnselected extends ObjectAck {
    @JsonCreator
    public ObjectUnselected(@JsonProperty("messageId") int messageId, @JsonProperty("checksum") String checksum) {
        super(messageId, 107, checksum);
    }
}
