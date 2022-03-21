package fi.whiteboardaalto.messages.server.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fi.whiteboardaalto.messages.server.Answer;

public class UserLeftBroadcast extends Answer {
    private String pseudo;

    @JsonCreator
    public UserLeftBroadcast(@JsonProperty("messageId") int messageId, @JsonProperty("pseudo") String pseudo) {
        super(messageId, 304);
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
