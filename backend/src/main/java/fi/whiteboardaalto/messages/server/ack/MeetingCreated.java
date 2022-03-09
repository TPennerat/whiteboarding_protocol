package fi.whiteboardaalto.messages.server.ack;

public class MeetingCreated extends SessionAck {
    public MeetingCreated(int messageId, String meetingId, String userId) {
        super(messageId, meetingId, userId);
    }
}
