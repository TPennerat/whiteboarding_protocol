package fi.whiteboardaalto.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class SuperMessage {

    private MessageType type;
    private JsonNode object;

    @JsonCreator
    public SuperMessage(@JsonProperty("type") MessageType type, @JsonProperty("object") JsonNode object) {
        this.type = type;
        this.object = object;
    }

    public MessageType getType() {
        return type;
    }

    public JsonNode getObject() {
        return object;
    }

    public void setObject(JsonNode object) {
        this.object = object;
    }

}