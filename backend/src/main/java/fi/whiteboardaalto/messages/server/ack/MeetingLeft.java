package fi.whiteboardaalto.messages.server.ack;

public class MeetingLeft extends SessionAck {
    public MeetingLeft(int messageId, String meetingId, String userId) {
        super(messageId, meetingId, userId);
    }
}
