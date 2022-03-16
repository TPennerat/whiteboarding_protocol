package fi.whiteboardaalto.messages;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.whiteboardaalto.messages.client.object.CreateObject;
import fi.whiteboardaalto.messages.client.session.CreateMeeting;
import fi.whiteboardaalto.messages.server.ack.object.ObjectCreated;

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
            @JsonSubTypes.Type(value = CreateMeeting.class, name = "CREATE_MEETING"),
            // Server messages
            @JsonSubTypes.Type(value = ObjectCreated.class, name = "OBJECT_CREATED"),
    })
    public void setMessage(Message message) {
        this.message = message;
    }

}
