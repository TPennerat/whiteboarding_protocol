package fi.whiteboardaalto.messages.client.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.messages.Message;

public class LeaveMeeting extends Message {
    private String meetingId;

    @JsonCreator
    public LeaveMeeting(@JsonProperty("messageId") int messageId, @JsonProperty("meetingId") String meetingId) {
        this.messageId = messageId;
        this.meetingId = meetingId;
    }

    public String getMeetingId() {
        return meetingId;
    }
}
