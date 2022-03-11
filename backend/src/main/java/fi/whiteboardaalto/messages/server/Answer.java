package fi.whiteboardaalto.messages.server;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.whiteboardaalto.messages.Message;
import fi.whiteboardaalto.messages.server.ack.object.ObjectCreated;
import fi.whiteboardaalto.messages.server.errors.BusyCoordinatesError;
import fi.whiteboardaalto.messages.server.errors.ServerFullError;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "answerType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = BusyCoordinatesError.class, name = "BUSY_COORDINATES_ERROR"),
        @JsonSubTypes.Type(value = ServerFullError.class, name = "SERVER_FULL_ERROR"),
        @JsonSubTypes.Type(value = ObjectCreated.class, name = "OBJECT_CREATED")
})
public class Answer extends Message {
    private AnswerType answerType;
    private int code;

    public Answer(int messageId, AnswerType answerType, int code) {
        super(messageId);
        this.answerType = answerType;
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @JsonProperty("answerType")
    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

}
