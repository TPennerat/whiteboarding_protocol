package fi.whiteboardaalto.messages;

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
import fi.whiteboardaalto.messages.server.update.BoardUpdate;

public class SuperMessage {
    private MessageType messageType;
    private Message message;

    @JsonCreator
    public SuperMessage(@JsonProperty("messageType") MessageType messageType, @JsonProperty("message") Message message) {
        this.messageType = messageType;
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "messageType")
    @JsonSubTypes(value = {
            // Client messages
            @JsonSubTypes.Type(value = CreateObject.class, name = "CREATE_OBJECT"),
            @JsonSubTypes.Type(value = SelectObject.class, name = "SELECT"),
            @JsonSubTypes.Type(value = DeleteObject.class, name = "DELETE"),
            @JsonSubTypes.Type(value = EditObject.class, name = "EDIT"),
            @JsonSubTypes.Type(value = CreateMeeting.class, name = "CREATE_MEETING"),
            @JsonSubTypes.Type(value = JoinMeeting.class, name = "JOIN_MEETING"),
            @JsonSubTypes.Type(value = LeaveMeeting.class, name = "LEAVE_MEETING"),
            @JsonSubTypes.Type(value = BoardUpdate.class, name = "BOARD_UPDATE"),
            // Server messages
            @JsonSubTypes.Type(value = ObjectCreated.class, name = "OBJECT_CREATED")
    })
    public void setMessage(Message message) {
        this.message = message;
    }

}
