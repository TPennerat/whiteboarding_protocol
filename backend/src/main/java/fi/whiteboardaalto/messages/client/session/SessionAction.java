package fi.whiteboardaalto.messages.client.session;

import fi.whiteboardaalto.messages.client.ActionMessage;

public class SessionAction extends ActionMessage {

    private String meetingId;

    public SessionAction(int messageId, String userId, String meetingId) {
        super(messageId, userId);
        this.meetingId = meetingId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

}
