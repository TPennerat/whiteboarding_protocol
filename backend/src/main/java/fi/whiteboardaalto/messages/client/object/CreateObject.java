package fi.whiteboardaalto.messages.client.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fi.whiteboardaalto.objects.*;

public class CreateObject extends ObjectAction {

    private BoardObject boardObject;
    private ObjectType objectType;

    @JsonCreator
    public CreateObject(@JsonProperty("messageId") int messageId, @JsonProperty("userId") String userId, @JsonProperty("objectId") String objectId, @JsonProperty("objectType") ObjectType objectType, @JsonProperty("boardObject") BoardObject boardObject) {
        super(messageId, userId, objectId);
        this.boardObject = boardObject;
        this.objectType = objectType;
    }

    public BoardObject getBoardObject() {
        return boardObject;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "objectType")
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = StickyNote.class, name = "STICKY_NOTE"),
            @JsonSubTypes.Type(value = Drawing.class, name = "DRAWING"),
            @JsonSubTypes.Type(value = Image.class, name = "IMAGE")
    })
    public void setBoardObject(BoardObject boardObject) {
        this.boardObject = boardObject;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }
}
