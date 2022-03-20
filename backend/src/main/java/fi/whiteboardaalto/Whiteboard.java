package fi.whiteboardaalto;

import fi.whiteboardaalto.messages.server.update.BoardUpdateComponent;
import fi.whiteboardaalto.objects.BoardObject;
import fi.whiteboardaalto.objects.Coordinates;
import fi.whiteboardaalto.objects.ObjectType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    public boolean coordinatesAreBusyByObject(BoardObject object) {
        for(BoardObject boardObject : boardObjects) {
            if(boardObject.getCoordinates().equals(object.getCoordinates())) {
                return true;
            }
        }
        return false;
    }

    public boolean coordinatesAreBusy(Coordinates coordinates) {
        for(BoardObject boardObject : boardObjects) {
            if(coordinates.equals(boardObject.getCoordinates())) {
                return true;
            }
        }
        return false;
    }

    public BoardObject getBoardObjectByObjectId (String objectId) {
            for (BoardObject boardObject : boardObjects) {
            if(boardObject.getObjectId().equals(objectId)) {
                return boardObject;
            }
        }
        return null;
    }

    public List<BoardUpdateComponent> getAllObjects() {
        List<BoardUpdateComponent> boardUpdateComponents = new ArrayList<BoardUpdateComponent>();
        for(BoardObject boardObject : boardObjects) {
            ObjectType objectType = BoardObject.objectTypeMapper(boardObject.getClass().getSimpleName());
            BoardUpdateComponent boardUpdateComponent = new BoardUpdateComponent(
                    objectType,
                    boardObject
            );
            boardUpdateComponents.add(boardUpdateComponent);
        }
        return boardUpdateComponents;
    }

}
