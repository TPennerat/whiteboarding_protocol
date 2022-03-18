package fi.whiteboardaalto.messages;

public enum MessageType {
    // CLIENT MESSAGES
    // Object-related messages
    CREATE_OBJECT,
    SELECT,
    UNSELECT,
    DELETE,
    EDIT,
    // Session-related messages
    JOIN_MEETING,
    LEAVE_MEETING,
    CREATE_MEETING,
    ADMIT_USER,
    REJECT_USER,
    INVITE_USER,

    // SERVER MESSAGES
    // Update messages
    CHANGE_BROADCAST,
    USER_BROADCAST,
    DELETE_BROADCAST,
    // Session-related message
    MEETING_LEFT,
    MEETING_JOINED,
    MEETING_CREATED,
    // Error message
    WRONG_FORMAT_ERROR,
    NON_EXISTING_MEETING_ERROR,
    USER_NOT_AUTH_ERROR,
    OBJECT_NOT_FOUND_ERROR,
    BUSY_OBJECT_ERROR,
    NON_EXISTENT_MEETING_ERROR,
    BUSY_PSEUDO_ERROR,
    MESSAGE_MALFORMED_ERROR,
    ALREADY_IN_MEETING_ERROR,
    MEETING_ALREADY_CREATED_ERROR,
    OBJECT_NOT_OWNED_ERROR,
    OBJECT_NOT_SELECTED_ERROR,
    // ACK MESSAGES
    OBJECT_CREATED,
    OBJECT_SELECTED,
    OBJECT_DELETED,
    OBJECT_UNSELECTED
}