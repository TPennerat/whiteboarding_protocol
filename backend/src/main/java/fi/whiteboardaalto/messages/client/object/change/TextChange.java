package fi.whiteboardaalto.messages.client.object.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TextChange extends Change {
    private String newText;

    @JsonCreator
    public TextChange(@JsonProperty("chanegId") int changeId, @JsonProperty("newText") String newText) {
        super(changeId);
        this.newText = newText;
    }

    public String getNewText() {
        return newText;
    }

    public void setNewText(String newText) {
        this.newText = newText;
    }
}
