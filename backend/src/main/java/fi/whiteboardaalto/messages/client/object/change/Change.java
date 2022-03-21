package fi.whiteboardaalto.messages.client.object.change;

public class Change {
    private int changeId;

    public Change(int changeId) {
        this.changeId = changeId;
    }

    public int getChangeId() {
        return changeId;
    }

    public void setChangeId(int changeId) {
        this.changeId = changeId;
    }
}
