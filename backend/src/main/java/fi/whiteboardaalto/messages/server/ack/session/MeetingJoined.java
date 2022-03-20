package fi.whiteboardaalto.messages.server.ack.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MeetingJoined extends SessionAck {
    @JsonCreator
    public MeetingJoined(@JsonProperty("messageId") int messageId, @JsonProperty("meetingId") String meetingId, @JsonProperty("userId") String userId) {
        super(messageId, 302, meetingId, userId);
    }
}
