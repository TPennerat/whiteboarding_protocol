package fi.whiteboardaalto.messages.server.update;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.messages.server.Answer;

import java.util.List;

public class BoardUpdate extends Answer {
    private List<BoardUpdateComponent> boardUpdateComponents;

    @JsonCreator
    public BoardUpdate(@JsonProperty("messageId") int messageId, @JsonProperty("boardUpdateComponents") List<BoardUpdateComponent> boardUpdateComponents) {
        super(messageId, 404);
        this.boardUpdateComponents = boardUpdateComponents;
    }

    public List<BoardUpdateComponent> getBoardUpdateComponents() {
        return boardUpdateComponents;
    }

    public void setBoardUpdateComponents(List<BoardUpdateComponent> boardUpdateComponents) {
        this.boardUpdateComponents = boardUpdateComponents;
    }

}
