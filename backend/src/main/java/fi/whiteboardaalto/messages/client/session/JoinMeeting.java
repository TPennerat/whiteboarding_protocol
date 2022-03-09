package fi.whiteboardaalto.messages.client.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.messages.Message;

public class JoinMeeting extends Message {
    private String meetingId;
    private String pseudo;

    @JsonCreator
    public JoinMeeting(@JsonProperty("messageId") int messageId, @JsonProperty("meetingId") String meetingId, @JsonProperty("pseudo") String pseudo) {
        this.messageId = messageId;
        this.meetingId = meetingId;
        this.pseudo = pseudo;
    }

    public int getMessageId() {
        return messageId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getPseudo() {
        return pseudo;
    }
}
