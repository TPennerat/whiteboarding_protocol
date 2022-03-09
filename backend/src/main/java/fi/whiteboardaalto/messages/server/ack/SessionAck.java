package fi.whiteboardaalto.messages.server.ack;

import fi.whiteboardaalto.messages.Message;

public class SessionAck extends Message {
    private String meetingId;
    private String userId;

    public SessionAck(int messageId, String meetingId, String userId) {
        this.messageId = messageId;
        this.meetingId = meetingId;
        this.userId = userId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getUserId() {
        return userId;
    }


}
