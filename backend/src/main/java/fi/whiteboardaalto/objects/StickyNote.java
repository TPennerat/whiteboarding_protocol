package fi.whiteboardaalto.objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class StickyNote extends BoardObject {
    private String text;
    private String font;
    private Coordinates size;

    @JsonCreator
    public StickyNote(@JsonProperty("objectId") int objectId, @JsonProperty("ownerId") String ownerId, @JsonProperty("isLocked") Boolean isLocked, @JsonProperty("coordinates") Coordinates coordinates, @JsonProperty("clour") Colour colour, @JsonProperty("text") String text, @JsonProperty("font") String font, @JsonProperty("size") Coordinates size) {
        super(objectId, ownerId, isLocked, coordinates, colour);
        this.text = text;
        this.font = font;
        this.size = size;
    }

    public String getText() {
        return text;
    }

    public String getFont() {
        return font;
    }

    public Coordinates getSize() {
        return size;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setFont(String font) {
        this.font = font;
    }

    public void setSize(Coordinates size) {
        this.size = size;
    }
}
