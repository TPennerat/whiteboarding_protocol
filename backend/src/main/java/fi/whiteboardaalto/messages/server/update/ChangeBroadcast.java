package fi.whiteboardaalto.messages.server.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.messages.server.Answer;
import fi.whiteboardaalto.objects.BoardObject;

public class ChangeBroadcast extends Answer {

    private BoardObject boardObject;

    @JsonCreator
    public ChangeBroadcast(@JsonProperty("messageId") int messageId, @JsonProperty("boardObject") BoardObject boardObject) {
        super(messageId, 401);
        this.boardObject = boardObject;
    }

    public BoardObject getBoardObject() {
        return boardObject;
    }

    public void setBoardObject(BoardObject boardObject) {
        this.boardObject = boardObject;
    }
}
