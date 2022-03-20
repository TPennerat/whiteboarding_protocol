package fi.whiteboardaalto.messages.client.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateMeeting extends SessionAction {

    private String pseudo;

    @JsonCreator
    public CreateMeeting(@JsonProperty("messageId") int messageId, @JsonProperty("userId") String userId, @JsonProperty("meetingId") String meetingId, @JsonProperty("pseudo") String pseudo) {
        super(messageId, userId, meetingId);
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

}
