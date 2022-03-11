package fi.whiteboardaalto.messages.server;

public enum AnswerType {
    // Errors
    BUSY_COORDINATES_ERROR,
    SERVER_FULL_ERROR,
    // ACKs
    OBJECT_CREATED,
    MEETING_CREATED
}
