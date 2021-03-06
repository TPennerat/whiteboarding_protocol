package fi.whiteboardaalto.messages.server.ack.session;

import fi.whiteboardaalto.messages.server.Answer;

public class SessionAck extends Answer {
    private String meetingId;
    private String userId;

    public SessionAck(int messageId, int code, String meetingId, String userId) {
        super(messageId, code);
        this.meetingId = meetingId;
        this.userId = userId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
