package fi.whiteboardaalto.messages.server.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import fi.whiteboardaalto.messages.client.object.CreateObject;
import fi.whiteboardaalto.messages.client.object.DeleteObject;
import fi.whiteboardaalto.messages.client.object.EditObject;
import fi.whiteboardaalto.messages.client.object.SelectObject;
import fi.whiteboardaalto.messages.client.session.CreateMeeting;
import fi.whiteboardaalto.messages.client.session.JoinMeeting;
import fi.whiteboardaalto.messages.client.session.LeaveMeeting;
import fi.whiteboardaalto.messages.server.ack.object.ObjectCreated;
import fi.whiteboardaalto.objects.*;

public class BoardUpdateComponent {
    private ObjectType objectType;
    private BoardObject boardObject;

    @JsonCreator
    public BoardUpdateComponent(@JsonProperty("objectType") ObjectType objectType, @JsonProperty("boardObject") BoardObject boardObject) {
        this.objectType = objectType;
        this.boardObject = boardObject;
    }

    public ObjectType getObjectType() {
        return objectType;
    }

    public void setObjectType(ObjectType objectType) {
        this.objectType = objectType;
    }

    public BoardObject getBoardObject() {
        return boardObject;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "objectType")
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = StickyNote.class, name = "STICKY_NOTE"),
            @JsonSubTypes.Type(value = Image.class, name = "IMAGE"),
            @JsonSubTypes.Type(value = Drawing.class, name = "DRAWING"),
    })
    public void setBoardObject(BoardObject boardObject) {
        this.boardObject = boardObject;
    }
}
