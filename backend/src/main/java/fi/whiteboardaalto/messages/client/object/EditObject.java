package fi.whiteboardaalto.messages.client.object;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import fi.whiteboardaalto.messages.client.object.change.Change;
import fi.whiteboardaalto.messages.client.object.change.EditType;
import fi.whiteboardaalto.messages.client.object.change.PositionChange;

public class EditObject extends ObjectAction {

    private Change change;
    private EditType editType;

    @JsonCreator
    public EditObject(@JsonProperty("messageId") int messageId, @JsonProperty("userId") String userId, @JsonProperty("objectId") String objectId, @JsonProperty("editType") EditType editType, @JsonProperty("change") Change change) {
        super(messageId, userId, objectId);
        this.editType = editType;
        this.change = change;
    }

    public Change getChange() {
        return change;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "editType")
    @JsonSubTypes(value = {
            @JsonSubTypes.Type(value = PositionChange.class, name = "POSITION_CHANGE")
    })
    public void setChange(Change change) {
        this.change = change;
    }

    public EditType getEditType() {
        return editType;
    }

    public void setEditType(EditType editType) {
        this.editType = editType;
    }

}
