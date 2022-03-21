package fi.whiteboardaalto.objects;

public class Image extends BoardObject {
    private String comment;
    private String stringImage;
    private Extension extension;

    public Image(String objectId, String ownerId, Boolean isLocked, Coordinates coordinates, Colour colour, String comment, String stringImage, Extension extension) {
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
