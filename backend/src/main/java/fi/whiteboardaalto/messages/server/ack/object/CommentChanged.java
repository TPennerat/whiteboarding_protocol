package fi.whiteboardaalto.messages.server.ack.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentChanged extends ObjectAck {
    @JsonCreator
    public CommentChanged(@JsonProperty("messageId") int messageId, @JsonProperty("checksum") String checksum) {
        super(messageId, 102, checksum);
    }
}
