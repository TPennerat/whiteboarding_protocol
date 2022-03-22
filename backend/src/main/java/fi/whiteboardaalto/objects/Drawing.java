package fi.whiteboardaalto.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Drawing extends BoardObject {

    private List<Coordinates> pointList;

    @JsonCreator
    public Drawing(@JsonProperty("objectId") String objectId, @JsonProperty("ownerId") String ownerId, @JsonProperty("isLocked") Boolean isLocked, @JsonProperty("coordinates") Coordinates coordinates, @JsonProperty("colour") Colour colour, List<Coordinates> pointList) {
        super(objectId, ownerId, isLocked, coordinates, colour);
        this.pointList = pointList;
    }

}
