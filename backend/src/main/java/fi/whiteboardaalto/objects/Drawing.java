package fi.whiteboardaalto.objects;

public class Drawing extends BoardObject {
    public Drawing(String objectId, String ownerId, Boolean isLocked, Coordinates coordinates, Colour colour) {
        super(objectId, ownerId, isLocked, coordinates, colour);
    }
}
