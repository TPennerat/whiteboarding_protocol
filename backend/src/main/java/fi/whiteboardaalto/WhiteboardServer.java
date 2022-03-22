package fi.whiteboardaalto;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import fi.whiteboardaalto.logging.Colour;
import fi.whiteboardaalto.logging.ConsoleLogger;
import fi.whiteboardaalto.messages.Message;
import fi.whiteboardaalto.messages.MessageType;
import fi.whiteboardaalto.messages.SuperMessage;
import fi.whiteboardaalto.messages.client.object.*;
import fi.whiteboardaalto.messages.client.object.change.ColourChange;
import fi.whiteboardaalto.messages.client.object.change.CommentChange;
import fi.whiteboardaalto.messages.client.object.change.PositionChange;
import fi.whiteboardaalto.messages.client.object.change.TextChange;
import fi.whiteboardaalto.messages.client.session.CreateMeeting;
import fi.whiteboardaalto.messages.client.session.JoinMeeting;
import fi.whiteboardaalto.messages.client.session.LeaveMeeting;
import fi.whiteboardaalto.messages.server.ack.object.*;
import fi.whiteboardaalto.messages.server.ack.session.MeetingCreated;
import fi.whiteboardaalto.messages.server.ack.session.MeetingJoined;
import fi.whiteboardaalto.messages.server.ack.session.MeetingLeft;
import fi.whiteboardaalto.messages.server.errors.*;
import fi.whiteboardaalto.messages.server.errors.Error;
import fi.whiteboardaalto.messages.server.update.*;
import fi.whiteboardaalto.objects.*;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.Text;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class WhiteboardServer extends WebSocketServer {

    private HashMap<WebSocket, User> users;
    private Set<WebSocket> conns;
    private Set<Meeting> meetings;

    private final ObjectMapper mapper;

    public WhiteboardServer(int port) {
        super(new InetSocketAddress(port));
        this.mapper = new ObjectMapper();
        conns = new HashSet<>();
        meetings = new HashSet<>();
        users = new HashMap<WebSocket, User>();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        conns.add(webSocket);
        String toDisplay = "[<>] New connection from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + webSocket.getRemoteSocketAddress().getPort();
        ConsoleLogger.loggConsole(toDisplay, Colour.CYAN);
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        User existingUser = users.get(conn);
        if(existingUser != null) {
            Meeting meeting = findMeetingByUserId(existingUser.getUserId());
            if(meeting.getUsers().contains(existingUser)) {
                // If the user is a not a host, we simply remove it from the users set
                meeting.getUsers().remove(existingUser);
                UserLeftBroadcast userLeftBroadcast = new UserLeftBroadcast(messageIdGenerator(), existingUser.getPseudo());
                broadcastMessageToOthers(userLeftBroadcast, conn, MessageType.USER_LEFT_BROADCAST);
            } else if (meeting.getHost() == existingUser) {
                // If the host is the last one in the meeting
                if(meeting.getTotalUsers() == 1) {
                    // We need to remove the meeting from the meeting the server is hosting
                    ConsoleLogger.loggConsole("[i] The meeting " + meeting.getMeetingId() + " will now be deleted.", Colour.WHITE);
                    meetings.remove(meeting);
                } else {
                    // If the host is not the last one, then we simply elect another host
                    meeting.transferHost();
                    // We update everyone (including the host) that there is a new host
                    HostBroadcast hostBroadcast = new HostBroadcast(messageIdGenerator(), meeting.getHost().getPseudo());
                    broadcastMessageToAll(hostBroadcast, meeting, MessageType.HOST_BROADCAST);
                }
            }
        }
        conns.remove(conn);
        ConsoleLogger.loggConsole("[<>] Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort(), Colour.YELLOW);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        SuperMessage superMessage = superMessageDeserialize(message);

        // We need to check if the message sent has some content and is properly formed
        if(superMessage == null || superMessage.getMessage() == null) {
            sendMessage(conn, new MessageMalformedError(messageIdGenerator()), MessageType.MESSAGE_MALFORMED_ERROR);
            return;
        }

        User existingUser;
        Meeting meeting;
        BoardObject boardObject;
        int messageIdAck;

        switch(superMessage.getMessageType()) {
            case CREATE_OBJECT:
                ConsoleLogger.loggConsole("[$] CREATE_OBJECT request received", Colour.PURPLE);
                CreateObject createObject = (CreateObject) superMessage.getMessage();
                // Need to check if the user is authenticated with the userID from the request
                if(!isUserAuth(createObject.getUserId(), conn)) {
                    sendMessage(conn, new UserNotAuthError(createObject.getMessageId()+1), MessageType.USER_NOT_AUTH_ERROR);
                    break;
                }
                if(createObject.getObjectId().length() != 0 || createObject.getBoardObject().getObjectId() .length()!= 0) {
                    sendMessage(conn, new MessageMalformedError(createObject.getMessageId()+1), MessageType.MESSAGE_MALFORMED_ERROR);
                    break;
                }
                addObjectToBoard(createObject, conn);
                ConsoleLogger.loggConsole(toString(), Colour.DEFAULT);
                break;
            case CREATE_MEETING:
                String networkId = conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort();
                ConsoleLogger.loggConsole("[$] CREATE_MEETING request received from " + networkId + ".", Colour.PURPLE);
                CreateMeeting createMeeting = (CreateMeeting) superMessage.getMessage();
                messageIdAck = createMeeting.getMessageId()+1;
                if(users.get(conn) == null) {
                    if(meetings.size()+1 <= 5) {
                        // 1st step: create new user, who will be the host of the meeting, and add it to server
                        User host = new User(idGenerator(IdType.USER_ID), createMeeting.getPseudo());
                        users.put(conn, host);
                        // 2nd step: create new meeting and add it to the server's hosted meetings
                        meeting = new Meeting(idGenerator(IdType.MEETING_ID), host);
                        meeting.setHost(host);
                        meetings.add(meeting);
                        ConsoleLogger.loggConsole("[+] New meeting created by " + host.getPseudo() + ": " + meeting.getMeetingId(), Colour.GREEN);
                        // 3rd step: send back to the host a confirmation message
                        MeetingCreated meetingCreated = new MeetingCreated(messageIdAck, meeting.getMeetingId(), host.getUserId());
                        sendMessage(conn, meetingCreated, MessageType.MEETING_CREATED);
                        ConsoleLogger.loggConsole(toString(), Colour.DEFAULT);
                    } else { ServerFullError error = new ServerFullError(messageIdAck); }
                } else sendMessage(conn, new MeetingAlreadyCreatedError(createMeeting.getMessageId()+1), MessageType.MEETING_ALREADY_CREATED_ERROR);
                break;
            case JOIN_MEETING:
                JoinMeeting joinMeeting = (JoinMeeting) superMessage.getMessage();
                meeting = findMeetingByMeetingId(joinMeeting.getMeetingId());
                if(meeting != null) {
                    if(users.get(conn) == null) { // If this is not null, it means this connection has already created a user
                        if(!meeting.pseudoAlreadyExists(joinMeeting.getPseudo())) {
                            User newUser = new User(idGenerator(IdType.USER_ID), joinMeeting.getPseudo());
                            meeting.getUsers().add(newUser);
                            users.put(conn, newUser);
                            // Sending the confirmation the meeting was joined
                            MeetingJoined meetingJoined = new MeetingJoined(joinMeeting.getMessageId()+1, meeting.getMeetingId(), newUser.getUserId());
                            sendMessage(conn, meetingJoined, MessageType.MEETING_JOINED);
                            ConsoleLogger.loggConsole("[+] " + newUser.getPseudo() + " joined the following meeting: " + meeting.getMeetingId(), Colour.GREEN);
                            // We also need to send the user all the whiteboard objects
                            List<BoardUpdateComponent> components = meeting.getWhiteboard().getAllObjects();
                            BoardUpdate boardUpdate = new BoardUpdate(
                                    joinMeeting.getMessageId()+2,
                                    components
                            );
                            sendMessage(conn, boardUpdate, MessageType.BOARD_UPDATE);
                            // Broadcasting the new user to the existing users
                            UserBroadcast userBroadcast = new UserBroadcast(messageIdGenerator(), newUser.getPseudo());
                            broadcastMessageToOthers(userBroadcast, conn, MessageType.USER_BROADCAST);
                            ConsoleLogger.loggConsole(toString(), Colour.DEFAULT);
                        } else sendMessage(conn, new BusyPseudoError(joinMeeting.getMessageId()+1), MessageType.BUSY_PSEUDO_ERROR);
                    } else sendMessage(conn, new AlreadyInMeetingError(joinMeeting.getMessageId()+1), MessageType.ALREADY_IN_MEETING_ERROR);
                } else sendMessage(conn, new NonExistentMeetingError(joinMeeting.getMessageId()+1), MessageType.NON_EXISTENT_MEETING_ERROR);
                break;
            case LEAVE_MEETING:
                /*
                    If the client sends a LeaveMeeting message and doesn't wait for the answer to close the socket,
                    a Runtime error is thrown as the connection doesn't exist anymore.
                    => FIX?
                 */
                // Checking if the user is authenticated
                LeaveMeeting leaveMeeting = (LeaveMeeting) superMessage.getMessage();
                if(!isUserAuth(leaveMeeting.getUserId(), conn)) {
                    sendMessage(conn, new UserNotAuthError(leaveMeeting.getMessageId()+1), MessageType.USER_NOT_AUTH_ERROR);
                    break;
                }
                // Meeting indicated in the message
                meeting = findMeetingByMeetingId(leaveMeeting.getMeetingId());
                // Meeting in which the user is
                Meeting meetingFromUser = findMeetingByUserId(users.get(conn).getUserId());
                // Checking if the meetingID is valid (right length)
                if(!(leaveMeeting.getMeetingId().length() == 16)) {
                    sendMessage(conn, new MessageMalformedError(leaveMeeting.getMessageId()+1), MessageType.MESSAGE_MALFORMED_ERROR);
                    break;
                }
                // Checking if the meeting exists
                if(meeting == null) {
                    sendMessage(conn, new NonExistentMeetingError(leaveMeeting.getMessageId()+1), MessageType.NON_EXISTING_MEETING_ERROR);
                    break;
                }
                // Checking if the meeting provided by the user is the right one
                if(!meeting.getMeetingId().equals(meetingFromUser.getMeetingId())) {
                    sendMessage(conn, new UserNotInMeetingError(leaveMeeting.getMessageId()+1), MessageType.USER_NOT_IN_MEETING_ERROR);
                    break;
                }
                String pseudo = users.get(conn).getPseudo();
                // Sending confirmation that the user left the meeting
                MeetingLeft meetingLeft = new MeetingLeft(
                        leaveMeeting.getMessageId()+1,
                        leaveMeeting.getMeetingId(),
                        leaveMeeting.getUserId()
                );
                sendMessage(conn, meetingLeft, MessageType.MEETING_LEFT);
                // Closing the connection
                conn.close();
                ConsoleLogger.loggConsole("[-] " + pseudo + " left the following meeting: " + leaveMeeting.getMeetingId(), Colour.YELLOW);
                // Broadcasting that the user has left
                UserLeftBroadcast userLeftBroadcast = new UserLeftBroadcast(messageIdGenerator(), pseudo);
                broadcastMessageToOthers(userLeftBroadcast, conn, MessageType.USER_LEFT_BROADCAST);
                ConsoleLogger.loggConsole(toString(), Colour.DEFAULT);
                break;
            case SELECT:
                SelectObject selectObject = (SelectObject) superMessage.getMessage();
                if(!isUserAuth(selectObject.getUserId(), conn)) {
                    sendMessage(conn, new UserNotAuthError(selectObject.getMessageId()+1), MessageType.USER_NOT_AUTH_ERROR);
                    break;
                }
                existingUser = users.get(conn);
                meeting = findMeetingByUserId(existingUser.getUserId());
                boardObject = meeting.getWhiteboard().getBoardObjectByObjectId(selectObject.getObjectId());
                if(boardObject != null) {
                    if(!boardObject.getIsLocked()) {
                        // We need to update the following object's properties: isLocked & ownerId
                        boardObject.setIsLocked(true);
                        boardObject.setOwnerId(existingUser.getUserId());
                        // The checksum is the one of the object AFTER THE MODIFICATIONS
                        String checksum = generateSha256Hash(objectSerialize(boardObject));
                        ObjectSelected objectSelected = new ObjectSelected(selectObject.getMessageId()+1, checksum);
                        sendMessage(conn, objectSelected, MessageType.OBJECT_SELECTED);
                        ConsoleLogger.loggConsole("[i] The boardObject " + boardObject.getObjectId() + " has been selected by " + existingUser.getPseudo(), Colour.WHITE);
                        // Broadcasting the object with the new modifications
                        ChangeBroadcast changeBroadcast = new ChangeBroadcast(selectObject.getMessageId()+1, boardObject);
                        broadcastMessageToOthers(changeBroadcast, conn, MessageType.CHANGE_BROADCAST);
                    } else sendMessage(conn, new BusyObjectError(selectObject.getMessageId()+1), MessageType.BUSY_OBJECT_ERROR);
                } else sendMessage(conn, new ObjectNotFoundError(selectObject.getMessageId()+1), MessageType.OBJECT_NOT_FOUND_ERROR);
                break;
            case UNSELECT:
                UnselectObject unselectObject = (UnselectObject) superMessage.getMessage();
                if(!isUserAuth(unselectObject.getUserId(), conn)) {
                    sendMessage(conn, new UserNotAuthError(unselectObject.getMessageId()+1), MessageType.USER_NOT_AUTH_ERROR);
                    break;
                }
                existingUser = users.get(conn);
                meeting = findMeetingByUserId(existingUser.getUserId());
                boardObject = meeting.getWhiteboard().getBoardObjectByObjectId(unselectObject.getObjectId());
                if(boardObject != null) {
                    // If the object is locked
                    if (boardObject.getIsLocked()) {
                        //  If the object's owner is the same as the sender of the request
                        if (boardObject.getOwnerId().equals(existingUser.getUserId())) {
                            // Changing the state of the object to unselected
                            boardObject.setIsLocked(false);
                            // Removing the owner
                            boardObject.setOwnerId(null);
                            // Sending confirmation to the source
                            String checksum = generateSha256Hash(objectSerialize(boardObject));
                            ObjectUnselected objectUnselected = new ObjectUnselected(unselectObject.getMessageId() + 1, checksum);
                            sendMessage(conn, objectUnselected, MessageType.OBJECT_UNSELECTED);
                            ConsoleLogger.loggConsole("[i] The boardObject " + boardObject.getObjectId() + " has been unselected by " + existingUser.getPseudo(), Colour.WHITE);
                            // Broadcasting the object with the new modifications
                            ChangeBroadcast changeBroadcast = new ChangeBroadcast(messageIdGenerator(), boardObject);
                            broadcastMessageToOthers(changeBroadcast, conn, MessageType.CHANGE_BROADCAST);
                        } else sendMessage(conn, new ObjectNotOwnedError(unselectObject.getMessageId() + 1), MessageType.OBJECT_NOT_OWNED_ERROR);
                    } else sendMessage(conn, new ObjectNotSelectedError(unselectObject.getMessageId()+1), MessageType.OBJECT_NOT_SELECTED_ERROR);
                } else sendMessage(conn, new ObjectNotFoundError(unselectObject.getMessageId()+1), MessageType.OBJECT_NOT_FOUND_ERROR);
                break;
            case DELETE:
                DeleteObject deleteObject = (DeleteObject) superMessage.getMessage();
                if(!isUserAuth(deleteObject.getUserId(), conn)) {
                    sendMessage(conn, new UserNotAuthError(deleteObject.getMessageId()+1), MessageType.USER_NOT_AUTH_ERROR);
                    break;
                }
                existingUser = users.get(conn);
                meeting = findMeetingByUserId(existingUser.getUserId());
                boardObject = meeting.getWhiteboard().getBoardObjectByObjectId(deleteObject.getObjectId());
                if(boardObject != null) {
                    // We need to check if the object has been selected priorly
                    if(boardObject.getIsLocked()) {
                        // We also need to check if the owner is the same as the user ID from the request
                        if(boardObject.getOwnerId().equals(existingUser.getUserId())) {
                            // The checksum here is the one of the object before deletion (only case where that happens)
                            String checksum = generateSha256Hash(objectSerialize(boardObject));
                            // Sending confirmation to the source
                            meeting.getWhiteboard().getBoardObjects().remove(boardObject);
                            ObjectDeleted objectDeleted = new ObjectDeleted(deleteObject.getMessageId()+1, checksum);
                            sendMessage(conn, objectDeleted, MessageType.OBJECT_DELETED);
                            // Broadcasting the object with the new modifications
                            DeleteBroadcast deleteBroadcast = new DeleteBroadcast(messageIdGenerator(), deleteObject.getObjectId());
                            broadcastMessageToOthers(deleteBroadcast, conn, MessageType.DELETE_BROADCAST);
                            ConsoleLogger.loggConsole("[i] The boardObject " + boardObject.getObjectId() + " has been deleted by " + existingUser.getPseudo() + ".", Colour.WHITE);
                            ConsoleLogger.loggConsole(toString(), Colour.DEFAULT);
                        } else sendMessage(conn, new ObjectNotOwnedError(deleteObject.getMessageId()+1), MessageType.OBJECT_NOT_OWNED_ERROR);
                    } else sendMessage(conn, new ObjectNotSelectedError(deleteObject.getMessageId()+1), MessageType.OBJECT_NOT_SELECTED_ERROR);
                } else sendMessage(conn, new ObjectNotFoundError(deleteObject.getMessageId()+1), MessageType.OBJECT_NOT_FOUND_ERROR);
                break;
            case EDIT:
                EditObject editObject = (EditObject) superMessage.getMessage();
                if(!isUserAuth(editObject.getUserId(), conn)) {
                    sendMessage(conn, new UserNotAuthError(editObject.getMessageId()+1), MessageType.USER_NOT_AUTH_ERROR);
                    break;
                }
                existingUser = users.get(conn);
                meeting = findMeetingByUserId(existingUser.getUserId());
                boardObject = meeting.getWhiteboard().getBoardObjectByObjectId(editObject.getObjectId());
                // If the object is null, we send an error and then we break
                if(boardObject == null) {
                    sendMessage(conn, new ObjectNotFoundError(editObject.getMessageId()+1), MessageType.OBJECT_NOT_FOUND_ERROR);
                    break;
                }
                // We need to check also if the object is selected by the user already
                if(!boardObject.getIsLocked() || !boardObject.getOwnerId().equals(editObject.getUserId())) {
                    sendMessage(conn, new ObjectNotOwnedError(editObject.getMessageId()+1), MessageType.OBJECT_NOT_OWNED_ERROR);
                    break;
                }
                String checksum;
                // If we got here, it means that:
                //      => The user is allowed to perform the action, as it owns the object.
                switch(editObject.getEditType()) {
                    case POSITION_CHANGE:
                        PositionChange positionChange = (PositionChange) editObject.getChange();
                        // We need to check also if the new position chosen is not busy (occupied by another object).
                        if(meeting.getWhiteboard().coordinatesAreBusy(positionChange.getNewPosition())) {
                            sendMessage(conn, new BusyCoordinatesError(editObject.getMessageId()+1), MessageType.BUSY_COORDINATES_ERROR);
                            break;
                        }
                        boardObject.setCoordinates(positionChange.getNewPosition());
                        checksum = generateSha256Hash(objectSerialize(boardObject));
                        PositionChanged positionChanged = new PositionChanged(editObject.getMessageId()+1, checksum);
                        sendMessage(conn, positionChanged, MessageType.POSITION_CHANGED);
                        ConsoleLogger.loggConsole("[i] The boardObject " + boardObject.getObjectId() + " has been edited (position changed) by " + existingUser.getPseudo() + ".", Colour.WHITE);
                        break;
                    case COLOUR_CHANGE:
                        if(boardObject instanceof Image) {
                            sendMessage(conn, new ChangeNotAllowedError(editObject.getMessageId()+1), MessageType.CHANGE_NOT_ALLOWED_ERROR);
                            break;
                        }
                        ColourChange colourChange = (ColourChange) editObject.getChange();
                        boardObject.setColour(colourChange.getNewColour());
                        checksum = generateSha256Hash(objectSerialize(boardObject));
                        ColourChanged colourChanged = new ColourChanged(editObject.getMessageId()+1, checksum);
                        sendMessage(conn, colourChanged, MessageType.COLOR_CHANGED);
                        ConsoleLogger.loggConsole("[i] The boardObject " + boardObject.getObjectId() + " has been edited (colour changed) by " + existingUser.getPseudo() + ".", Colour.WHITE);
                        break;
                    case COMMENT_CHANGE:
                        if(boardObject instanceof Drawing || boardObject instanceof StickyNote) {
                            sendMessage(conn, new ChangeNotAllowedError(editObject.getMessageId()+1), MessageType.CHANGE_NOT_ALLOWED_ERROR);
                            break;
                        }
                        CommentChange commentChange = (CommentChange) editObject.getChange();
                        Image image = (Image) boardObject;
                        image.setComment(commentChange.getNewComment());
                        checksum = generateSha256Hash(objectSerialize(image));
                        CommentChanged commentChanged = new CommentChanged(editObject.getMessageId()+1, checksum);
                        sendMessage(conn, commentChanged, MessageType.COMMENT_CHANGED);
                        ConsoleLogger.loggConsole("[i] The " + image.getClass().getSimpleName() + " " + boardObject.getObjectId() + " has been edited (comment changed) by " + existingUser.getPseudo() + ".", Colour.WHITE);
                        break;
                    case TEXT_CHANGE:
                        if(boardObject instanceof Drawing || boardObject instanceof Image) {
                            sendMessage(conn, new ChangeNotAllowedError(editObject.getMessageId()+1), MessageType.CHANGE_NOT_ALLOWED_ERROR);
                            break;
                        }
                        TextChange textChange = (TextChange) editObject.getChange();
                        StickyNote stickyNote = (StickyNote) boardObject;
                        stickyNote.setText(textChange.getNewText());
                        checksum = generateSha256Hash(objectSerialize(stickyNote));
                        TextChanged textChanged = new TextChanged(editObject.getMessageId()+1, checksum);
                        sendMessage(conn, textChanged, MessageType.TEXT_CHANGED);
                        ConsoleLogger.loggConsole("[i] The " + stickyNote.getClass().getSimpleName() + " " + boardObject.getObjectId() + " has been edited (text changed) by " + existingUser.getPseudo() + ".", Colour.WHITE);
                        break;
                }
                ChangeBroadcast changeBroadcast = new ChangeBroadcast(messageIdGenerator(), boardObject);
                broadcastMessageToOthers(changeBroadcast, conn, MessageType.CHANGE_BROADCAST);
                break;
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {}

    @Override
    public void onStart() {
            ConsoleLogger.loggConsole("### TAT.IO - v0.2 ###", Colour.WHITE);
        ConsoleLogger.loggConsole("[i] Starting the server on port " + getPort() + "...", Colour.WHITE);
    }

    @Override
    public String toString() {
        StringBuilder toString;
        toString = new StringBuilder("[*] Current meetings:").append(System.lineSeparator());
        int i = 1;
        for(Meeting meeting : meetings) {
            toString.append("=> Meeting ").append(i).append(System.lineSeparator());
            toString.append("- Meeting ID: ").append(meeting.getMeetingId()).append(System.lineSeparator());
            toString.append("- Host: ").append(meeting.getHost().getPseudo()).append(System.lineSeparator());
            toString.append("- Current users:").append(System.lineSeparator());
            for (User user : meeting.getUsers()) {
                toString.append("> ").append(user.getPseudo()).append(System.lineSeparator());
            }
            toString.append("- Current objects:").append(System.lineSeparator());
            for (BoardObject boardObject : meeting.getWhiteboard().getBoardObjects()) {
                toString.append("> ")
                        .append(boardObject.getClass().getSimpleName())
                        .append(", ID: ").append(boardObject.getObjectId())
                        .append(", locked: ").append(boardObject.getIsLocked())
                        .append(", owner ID: ").append(boardObject.getOwnerId())
                        .append(System.lineSeparator());
            }
            toString.append("- Total users: ").append(meeting.getTotalUsers()).append(System.lineSeparator());
        }
        return toString.toString();
    }

    private void addObjectToBoard(CreateObject createObject, WebSocket conn) {

        ObjectType objectType = createObject.getObjectType();
        BoardObject boardObject = createObject.getBoardObject();
        User existingUser = users.get(conn);
        int messageIdAck = createObject.getMessageId()+1;
        Meeting meeting = findMeetingByUserId(existingUser.getUserId());

        String objectId = null;

        // We need to make sure the ID we generate for the object is unique
        boolean alreadyTaken = true;
        while(alreadyTaken) {
            objectId = idGenerator(IdType.OBJECT_ID);
            alreadyTaken = meeting.getWhiteboard().objectIdAlreadyTaken(objectId);
        }

        if(!meeting.getWhiteboard().coordinatesAreBusyByObject(boardObject)) {
            boardObject.setObjectId(objectId);
            boardObject.setIsLocked(true);
            boardObject.setOwnerId(existingUser.getUserId());

            String sha256hash = null;
            ChangeBroadcast changeBroadcast = null;

            switch (objectType) {
                case STICKY_NOTE -> {
                    StickyNote stickyNote = (StickyNote) boardObject;
                    meeting.getWhiteboard().getBoardObjects().add(stickyNote);
                    ConsoleLogger.loggConsole("[+] StickyNote created and added to meeting " + meeting.getMeetingId() + " by " + existingUser.getPseudo(), Colour.GREEN);
                    String serializedStickyNote = objectSerialize(stickyNote);
                    sha256hash = generateSha256Hash(serializedStickyNote);
                    changeBroadcast = new ChangeBroadcast(messageIdGenerator(), boardObject);
                }
                case IMAGE -> {
                    Image image = (Image) boardObject;
                    meeting.getWhiteboard().getBoardObjects().add(image);
                    ConsoleLogger.loggConsole("[+] Image created and added to meeting " + meeting.getMeetingId() + " by " + existingUser.getPseudo(), Colour.GREEN);
                    String serializedImage = objectSerialize(image);
                    sha256hash = generateSha256Hash(serializedImage);
                    changeBroadcast = new ChangeBroadcast(messageIdGenerator(), image);
                }
                case DRAWING -> {
                    Drawing drawing = (Drawing) boardObject;
                    meeting.getWhiteboard().getBoardObjects().add(drawing);
                    ConsoleLogger.loggConsole("[+] Drawing created and added to meeting " + meeting.getMeetingId() + " by " + existingUser.getPseudo(), Colour.GREEN);
                    String serializedDrawing = objectSerialize(drawing);
                    sha256hash = generateSha256Hash(serializedDrawing);
                    changeBroadcast = new ChangeBroadcast(messageIdGenerator(), drawing);
                }
            }
            ObjectCreated objectCreated = new ObjectCreated(messageIdAck, objectId, sha256hash);
            sendMessage(conn, objectCreated, MessageType.OBJECT_CREATED);
            broadcastMessageToOthers(changeBroadcast, conn, MessageType.CHANGE_BROADCAST);
        } else { sendMessage(conn, new BusyCoordinatesError(messageIdAck), MessageType.BUSY_COORDINATES_ERROR); }

    }

    private void broadcastMessageToOthers(Object object, WebSocket notToSendTo, MessageType messageType) {
        // Finding the meeting with all the users inside
        String userId = users.get(notToSendTo).getUserId();
        Meeting meeting = findMeetingByUserId(userId);
        // Broadcasting the message
        for (WebSocket webSocket : getAllSocketsFromMeeting(meeting)) {
            // If the source of the creation/change (a user) is the same as the one we're sending the message to,
            // we need to do nothing.
            if(notToSendTo != webSocket) {
                sendMessage(webSocket, object, messageType);
            }
        }
    }

    private void broadcastMessageToAll(Object object, Meeting meeting, MessageType messageType) {
        for (WebSocket webSocket : getAllSocketsFromMeeting(meeting)) {
                sendMessage(webSocket, object, messageType);
            }
        }

    private Set<WebSocket> getAllSocketsFromMeeting(Meeting meeting) {
        Set<WebSocket> set = new HashSet<WebSocket>();
        for(Map.Entry<WebSocket, User> entry : users.entrySet()) {
            if(Objects.equals(meeting.getHost(), entry.getValue())) {
                set.add(entry.getKey());
            }
            for(User userToAdd : meeting.getUsers()) {
                if (Objects.equals(userToAdd, entry.getValue()) || Objects.equals(meeting.getHost(), entry.getValue())) {
                    set.add(entry.getKey());
                }
            }
        }
        return set;
    }

    private void sendMessage(WebSocket conn, Object object, MessageType type) {
        try {
            SuperMessage superMessage = new SuperMessage(type, (Message) object);
            conn.send(mapper.writeValueAsString(superMessage));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private String objectSerialize(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private SuperMessage superMessageDeserialize(String object) {
        try {
            return mapper.readValue(object, SuperMessage.class);
        } catch (JsonProcessingException e) {
            if(e.getMessage().startsWith("No content to map")) System.err.println("[e] Error: Empty message received, can't deserialize.");
            else if (e.getMessage().startsWith("Unrecognized token")) System.err.println("[e] Error: Wrong message format.");
            else System.err.println("[e] Error in deserializing incoming JSON message: " + e.getMessage());
            return null;
        }
    }

    private boolean isUserAuth(String userId, WebSocket conn) {
        /*
            To check if a user is authenticated, we need to look at two things:
            -   The userID provided in the request exists on the server.
                => The user is in a meeting.
            -   The WebSocket used to send the request is the same as the one used
                when the user joined the meeting or created it.
                => Need to look in the users HashSet.
         */
        Meeting meeting = findMeetingByUserId(userId);
        if(meeting != null) {
            User user = users.get(conn);
            if(user != null) {
                return users.get(conn).getUserId().equals(userId);
            } else return false;
        } else {
            return false;
        }
    }

    private String idGenerator(IdType idType) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength;
        switch(idType) {
            case USER_ID -> targetStringLength = 8;
            case MEETING_ID -> targetStringLength = 16;
            case OBJECT_ID -> targetStringLength = 4;
            default -> targetStringLength = 0;
        }
        Random random = new Random();
        String id = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return id;
    }

    private int messageIdGenerator() {
        Random random = new Random();
        return random.nextInt(9999999);
    }

    private String generateSha256Hash(String serializedObject) {
        HashFunction hashFunction = Hashing.sha256();
        HashCode hash = hashFunction.hashString(serializedObject, StandardCharsets.UTF_8);
        return hash.toString();
    }

    /**
     * This method goes through all the meetings hosted by the server and returns a reference on the meeting
     * object for which the meeting ID is the same as the one provided in the parameters.
     * @param meetingId
     * @return meeting: a reference on the found meeting; null if nothing was found.
     */
    private Meeting findMeetingByMeetingId(String meetingId) {
        for (Meeting meeting : meetings) {
            if(meeting.getMeetingId().equals(meetingId)) {
                return meeting;
            }
        }
        return null;
    }

    /**
     * This method goes through all the meetings hosted by the servers and returns a reference on the meeting
     * object in which a user with the same userId exists.
     * @param userId
     * @return meeting: a reference on the found meeting; null if nothing was found.
     */
    private Meeting findMeetingByUserId(String userId) {
        for (Meeting meeting : meetings) {
            User host = meeting.getHost();
            if(host.getUserId().equals(userId)) return meeting;
            for(User user : meeting.getUsers()) {
                if(user.getUserId().equals(userId)) {
                    return meeting;
                }
            }
        }
        return null;
    }

}
