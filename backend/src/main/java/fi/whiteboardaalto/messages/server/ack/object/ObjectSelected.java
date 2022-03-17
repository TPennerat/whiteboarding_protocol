package fi.whiteboardaalto.messages.server.ack.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ObjectSelected extends ObjectAck {
    @JsonCreator
    public ObjectSelected(@JsonProperty("messageId") int messageId, @JsonProperty("checksum") String checksum) {
        super(messageId, 101, checksum);
    }
}
