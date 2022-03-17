package fi.whiteboardaalto;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import fi.whiteboardaalto.messages.Message;
import fi.whiteboardaalto.messages.MessageType;
import fi.whiteboardaalto.messages.SuperMessage;
import fi.whiteboardaalto.messages.client.object.CreateObject;
import fi.whiteboardaalto.messages.client.object.DeleteObject;
import fi.whiteboardaalto.messages.client.object.SelectObject;
import fi.whiteboardaalto.messages.client.object.UnselectObject;
import fi.whiteboardaalto.messages.client.session.CreateMeeting;
import fi.whiteboardaalto.messages.client.session.JoinMeeting;
import fi.whiteboardaalto.messages.server.ack.object.ObjectCreated;
import fi.whiteboardaalto.messages.server.ack.object.ObjectDeleted;
import fi.whiteboardaalto.messages.server.ack.object.ObjectSelected;
import fi.whiteboardaalto.messages.server.ack.object.ObjectUnselected;
import fi.whiteboardaalto.messages.server.ack.session.MeetingCreated;
import fi.whiteboardaalto.messages.server.ack.session.MeetingJoined;
import fi.whiteboardaalto.messages.server.errors.*;
import fi.whiteboardaalto.messages.server.update.ChangeBroadcast;
import fi.whiteboardaalto.messages.server.update.UserBroadcast;
import fi.whiteboardaalto.objects.BoardObject;
import fi.whiteboardaalto.objects.StickyNote;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
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
        System.out.println("New connection from " + webSocket.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + webSocket.getRemoteSocketAddress().getPort());
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        // If a user existed for this connection, we need to check if it was in a meeting or not
        User user = users.get(conn);
        if(user != null) {
            Meeting meeting = findMeetingByUserId(user.getUserId());
            if(meeting.getHost().getUserId().equals(user.getUserId())) {
                // If the user that was disconnected was the host, we need to transfer the host title to someone else
                meeting.transferHost();
            } else {
                // Otherwise, we still need to remove the user from the users set of the meeting
                meeting.getUsers().remove(user);
            }
            this.users.remove(user);
        }
        conns.remove(conn);
        // Remove the user (to code)
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort());
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

        switch(superMessage.getMessageType()) {
            case CREATE_OBJECT:
                System.out.println("[*] CREATE_OBJECT request received!");
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
                existingUser = users.get(conn);
                switch(createObject.getObjectType()) {
                    case STICKY_NOTE:
                        StickyNote stickyNote = (StickyNote) createObject.getBoardObject();
                        // Need to define custom exception: if meeting null?
                        meeting = findMeetingByUserId(existingUser.getUserId());
                        if(meeting == null) {System.err.println("[*] Error: User is not in any meeting."); break;}
                        int messageIdAck = createObject.getMessageId()+1;
                        if(!meeting.getWhiteboard().coordinatesAreBusy(stickyNote)) {
                            String objectId = idGenerator(IdType.OBJECT_ID);
                            // Preparing the sticky note before adding it in the objects list of the meeting
                            stickyNote.setObjectId(objectId);
                            stickyNote.setIsLocked(false);
                            stickyNote.setOwnerId(existingUser.getUserId());
                            // Adding the sticky note
                            meeting.getWhiteboard().getBoardObjects().add(stickyNote);
                            // Calculating hash
                            String serializedStickyNote = objectSerialize(stickyNote);
                            String sha256hash = generateSha256Hash(serializedStickyNote);
                            // Sending confirmation
                            ObjectCreated objectCreated = new ObjectCreated(messageIdAck, objectId, sha256hash);
                            System.out.println("[*] StickyNote created and added to meeting " + meeting.getMeetingId());
                            sendMessage(conn, objectCreated, MessageType.OBJECT_CREATED);
                            // Preparing the broadcast message
                            ChangeBroadcast changeBroadcast = new ChangeBroadcast(messageIdGenerator(), stickyNote);
                            broadcastMessage(changeBroadcast, conn, MessageType.CHANGE_BROADCAST);
                        } else { sendMessage(conn, new BusyCoordinatesError(messageIdAck), MessageType.OBJECT_CREATED); }
                        break;
                    case IMAGE:
                        break;
                }
                break;
            case CREATE_MEETING:
                System.out.println("[*] CREATE_MEETING request received!");
                CreateMeeting createMeeting = (CreateMeeting) superMessage.getMessage();
                int messageIdAck = createMeeting.getMessageId()+1;
                if(users.get(conn) == null) {
                    if(meetings.size()+1 <= 5) {
                        // 1st step: create new user, who will be the host of the meeting, and add it to server
                        User host = new User(idGenerator(IdType.USER_ID), createMeeting.getPseudo());
                        users.put(conn, host);
                        // 2nd step: create new meeting and add it to the server's hosted meetings
                        meeting = new Meeting(idGenerator(IdType.MEETING_ID), host);
                        meeting.setHost(host);
                        meetings.add(meeting);
                        System.out.println("[*] New meeting created: " + meeting.getMeetingId());
                        // 3rd step: send back to the host a confirmation message
                        MeetingCreated meetingCreated = new MeetingCreated(messageIdAck, meeting.getMeetingId(), host.getUserId());
                        sendMessage(conn, meetingCreated, MessageType.MEETING_CREATED);
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
                            MeetingJoined meetingJoined = new MeetingJoined(joinMeeting.getMessageId()+1, meeting.getMeetingId(), newUser.getUserId());
                            sendMessage(conn, meetingJoined, MessageType.MEETING_JOINED);
                            // Broadcasting the new user to the existing users
                            UserBroadcast userBroadcast = new UserBroadcast(messageIdGenerator(), newUser.getPseudo());
                            broadcastMessage(userBroadcast, conn, MessageType.USER_BROADCAST);
                            System.out.println(toString());
                        } else sendMessage(conn, new BusyPseudoError(joinMeeting.getMessageId()+1), MessageType.BUSY_PSEUDO_ERROR);
                    } else sendMessage(conn, new AlreadyInMeetingError(joinMeeting.getMessageId()+1), MessageType.ALREADY_IN_MEETING_ERROR);
                } else sendMessage(conn, new NonExistentMeetingError(joinMeeting.getMessageId()+1), MessageType.NON_EXISTENT_MEETING_ERROR);
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
                            boardObject.setIsLocked(true);
                            String checksum = generateSha256Hash(objectSerialize(boardObject));
                            ObjectUnselected objectUnselected = new ObjectUnselected(unselectObject.getMessageId() + 1, checksum);
                            sendMessage(conn, objectUnselected, MessageType.OBJECT_UNSELECTED);
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
                    if(!boardObject.getIsLocked()) {
                        // The checksum here is the one of the object before deletion.
                        String checksum = generateSha256Hash(objectSerialize(boardObject));
                        meeting.getWhiteboard().getBoardObjects().remove(boardObject);
                        ObjectDeleted objectDeleted = new ObjectDeleted(deleteObject.getMessageId()+1, checksum);
                        sendMessage(conn, objectDeleted, MessageType.OBJECT_DELETED);
                        System.out.println(toString());
                    } else sendMessage(conn, new BusyObjectError(deleteObject.getMessageId()+1), MessageType.BUSY_OBJECT_ERROR);
                } else sendMessage(conn, new ObjectNotFoundError(deleteObject.getMessageId()+1), MessageType.OBJECT_NOT_FOUND_ERROR);
                break;
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {}

    @Override
    public void onStart() {
        System.out.println("Starting the server...");
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

        }
        return toString.toString();
    }

    private void broadcastMessage(Object object, WebSocket notToSendTo, MessageType messageType) {
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

    private Set<WebSocket> getAllSocketsFromMeeting(Meeting meeting) {
        Set<WebSocket> set = new HashSet<WebSocket>();
        for(User userToAdd : meeting.getUsers()) {
            for (Map.Entry<WebSocket, User> entry : users.entrySet()) {
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
            System.err.println("[*] Error in deserializing incoming JSON message: " + e.getMessage());
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
