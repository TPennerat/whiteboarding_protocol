package fi.whiteboardaalto.messages.server.ack.session;

public class MeetingCreated extends SessionAck {
    public MeetingCreated(int messageId, String meetingId, String userId) {
        super(messageId, 301, meetingId, userId);
    }
}
