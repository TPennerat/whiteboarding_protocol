package fi.whiteboardaalto.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Image extends BoardObject {
    private String comment;
    private String stringImage;
    private Extension extension;

    @JsonCreator
    public Image(@JsonProperty("objectId") String objectId, @JsonProperty("ownerId") String ownerId, @JsonProperty("isLocked") Boolean isLocked, @JsonProperty("coordinates") Coordinates coordinates, @JsonProperty("colour") Colour colour, @JsonProperty("comment") String comment, @JsonProperty("stringImage") String stringImage, @JsonProperty("extension") Extension extension) {
        super(objectId, ownerId, isLocked, coordinates, colour);
        this.comment = comment;
        this.stringImage = stringImage;
        this.extension = extension;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStringImage() {
        return stringImage;
    }

    public void setStringImage(String stringImage) {
        this.stringImage = stringImage;
    }

    public Extension getExtension() {
        return extension;
    }

    public void setExtension(Extension extension) {
        this.extension = extension;
    }
}
