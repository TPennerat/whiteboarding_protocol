package fi.whiteboardaalto.messages.server.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fi.whiteboardaalto.messages.server.Answer;

public class HostBroadcast extends Answer {
    private String pseudo;

    @JsonCreator
    public HostBroadcast(@JsonProperty("messageId") int messageId, @JsonProperty("pseudo") String pseudo) {
        super(messageId, 305);
        this.pseudo = pseudo;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
}
