package fi.whiteboardaalto.messages.server.ack.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetingLeft extends SessionAck {
    @JsonCreator
    public MeetingLeft(@JsonProperty("messageId") int messageId, @JsonProperty("meetingId") String meetingId, @JsonProperty("userId") String userId) {
        super(messageId, 303, meetingId, userId);
    }
}
