package fi.whiteboardaalto.messages.client.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LeaveMeeting extends SessionAction {
    @JsonCreator
    public LeaveMeeting(@JsonProperty("messageId") int messageId, @JsonProperty("userId") String userId, @JsonProperty("meetingId") String meetingId) {
        super(messageId, userId, meetingId);
    }
}
