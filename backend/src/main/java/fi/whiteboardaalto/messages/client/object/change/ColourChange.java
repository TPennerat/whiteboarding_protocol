package fi.whiteboardaalto.messages.client.object.change;

import fi.whiteboardaalto.objects.Colour;

public class ColourChange extends Change {
    private Colour newColour;

    public ColourChange(int changeId, Colour newColour) {
        super(changeId);
        this.newColour = newColour;
    }

    public Colour getNewColour() {
        return newColour;
    }

    public void setNewColour(Colour newColour) {
        this.newColour = newColour;
    }

}
