package fi.whiteboardaalto.messages.server.ack.session;

import fi.whiteboardaalto.messages.server.AnswerType;

public class MeetingCreated extends SessionAck {
    public MeetingCreated(int messageId, String meetingId, String userId) {
        super(messageId, AnswerType.MEETING_CREATED, 102, meetingId, userId);
    }
}
