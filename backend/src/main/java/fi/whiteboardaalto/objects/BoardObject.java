package fi.whiteboardaalto.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

public class BoardObject {

    @JsonIgnore
    protected String objectId;
    protected String ownerId;
    protected Boolean isLocked;
    protected Coordinates coordinates;
    protected Colour colour;

    @JsonCreator
    public BoardObject(@JsonProperty("objectId") String objectId, @JsonProperty("ownerId") String ownerId, @JsonProperty("isLocked") Boolean isLocked, @JsonProperty("coordinates") Coordinates coordinates, @JsonProperty("colour") Colour colour) {
        this.objectId = objectId;
        this.ownerId = ownerId;
        this.isLocked = isLocked;
        this.coordinates = coordinates;
        this.colour = colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoardObject that = (BoardObject) o;
        return objectId == that.objectId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(objectId);
    }

    public String getObjectId() {
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

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setIsLocked(Boolean locked) {
        isLocked = locked;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

}
