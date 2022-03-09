package fi.whiteboardaalto.messages.client.session;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.messages.Message;

public class CreateMeeting extends Message {

    private String pseudo;

    @JsonCreator
    public CreateMeeting(@JsonProperty("pseudo") String pseudo, @JsonProperty("messageId") int messageId) {
        this.pseudo = pseudo;
        this.messageId = messageId;
    }

    public String getPseudo() {
        return pseudo;
    }

    public int getMessageId() {
        return messageId;
    }

}