package fi.whiteboardaalto.messages.client.object.change;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CommentChange extends Change {
    private String newComment;

    @JsonCreator
    public CommentChange(@JsonProperty("changeId") int changeId, @JsonProperty("newComment") String newComment) {
        super(changeId);
        this.newComment = newComment;
    }

    public String getNewComment() {
        return newComment;
    }

    public void setNewComment(String newComment) {
        this.newComment = newComment;
    }

}
