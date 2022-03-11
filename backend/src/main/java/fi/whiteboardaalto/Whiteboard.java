package fi.whiteboardaalto;

import fi.whiteboardaalto.objects.BoardObject;

import java.util.HashSet;
import java.util.Set;

public class Whiteboard {
    private Set<BoardObject> boardObjects;

    public Whiteboard() {
        this.boardObjects = new HashSet<>();
    }

    public Set<BoardObject> getBoardObjects() {
        return boardObjects;
    }

    public void setBoardObjects(Set<BoardObject> boardObjects) {
        this.boardObjects = boardObjects;
    }

    public boolean coordinatesAreBusy(BoardObject object) {
        for(BoardObject boardObject : boardObjects) {
            if(boardObject.getCoordinates().equals(object.getCoordinates())) {
                return true;
            }
        }
        return false;
    }
}
