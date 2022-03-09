package fi.whiteboardaalto.messages.server.errors;

public class WrongFormatError extends Error {

    public WrongFormatError(int code, String message) {
        super(code, message);
    }
}
