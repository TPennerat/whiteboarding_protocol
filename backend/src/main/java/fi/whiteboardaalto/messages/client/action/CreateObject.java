package fi.whiteboardaalto.messages.client.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class CreateObject extends ActionMessage {

    private JsonNode whiteBoardObject;

    @JsonCreator
    public CreateObject(@JsonProperty("messageId") int messageId, @JsonProperty("userId") String userId, @JsonProperty("object") JsonNode whiteBoardObject) {
        super(messageId, userId);
        this.whiteBoardObject = whiteBoardObject;
    }

    public JsonNode getWhiteBoardObject() {
        return whiteBoardObject;
    }
}