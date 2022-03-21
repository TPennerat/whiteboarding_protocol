package fi.whiteboardaalto.messages.server.errors;

public class ObjectNotSelectedError extends Error {
    public ObjectNotSelectedError(int messageId) {
        super(messageId, 435, "Object not selected.");
    }
}
