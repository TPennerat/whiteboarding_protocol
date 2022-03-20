package fi.whiteboardaalto.messages.client.object.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fi.whiteboardaalto.objects.Coordinates;

public class PositionChange extends Change {

    private Coordinates newPosition;

    @JsonCreator
    public PositionChange(@JsonProperty("changeId") int changeId, @JsonProperty("newPosition") Coordinates newPosition) {
        super(changeId);
        this.newPosition = newPosition;
    }

    public Coordinates getNewPosition() {
        return newPosition;
    }

    public void setNewPosition(Coordinates newPosition) {
        this.newPosition = newPosition;
    }
}
