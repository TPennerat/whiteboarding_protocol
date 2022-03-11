package fi.whiteboardaalto.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;

public class BoardObject {

    @JsonIgnore
    protected int objectId;
    protected String ownerId;
    protected Boolean isLocked;
    protected Coordinates coordinates;
    protected Colour colour;

    @JsonCreator
    public BoardObject(@JsonProperty("objectId") int objectId, @JsonProperty("ownerId") String ownerId, @JsonProperty("isLocked") Boolean isLocked, @JsonProperty("coordinates") Coordinates coordinates, @JsonProperty("colour") Colour colour) {
        this.objectId = objectId;
        this.ownerId = ownerId;
        this.isLocked = isLocked;
        this.coordinates = coordinates;
        this.colour = colour;
    }

    public int getObjectId() {
        return objectId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public Boolean getIsLocked() {
        return isLocked;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public Colour getColour() {
        return colour;
    }

    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

}
