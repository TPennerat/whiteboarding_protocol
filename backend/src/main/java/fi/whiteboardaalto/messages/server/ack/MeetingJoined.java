package fi.whiteboardaalto.messages.server.ack;

public class MeetingJoined extends SessionAck {
    public MeetingJoined(int messageId, String meetingId, String userId) {
        super(messageId, meetingId, userId);
    }
}
