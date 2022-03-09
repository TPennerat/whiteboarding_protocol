package fi.whiteboardaalto.messages.server.errors;

public class NonExistentMeeting extends Error {
    public NonExistentMeeting(int code, String message) {
        super(code, message);
    }
}
