package fi.whiteboardaalto;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import fi.whiteboardaalto.messages.Message;
import fi.whiteboardaalto.messages.MessageType;
import fi.whiteboardaalto.messages.SuperMessage;
import fi.whiteboardaalto.messages.client.object.CreateObject;
import fi.whiteboardaalto.messages.client.session.CreateMeeting;
import fi.whiteboardaalto.messages.server.ack.object.ObjectCreated;
import fi.whiteboardaalto.messages.server.ack.session.MeetingCreated;
import fi.whiteboardaalto.messages.server.errors.BusyCoordinatesError;
import fi.whiteboardaalto.messages.server.errors.ServerFullError;
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
        conns.remove(conn);
        // Remove the user (to code)
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        SuperMessage superMessage = superMessageDeserialize(message);
        switch(superMessage.getMessageType()) {
            case CREATE_OBJECT:
                System.out.println("[*] CREATE_OBJECT request received!");
                User existingUser = users.get(conn);
                if(existingUser == null) {
                    System.err.println("[*] Error: User doesn't exist.");
                    break;
                }
                CreateObject createObject = (CreateObject) superMessage.getMessage();
                switch(createObject.getObjectType()) {
                    case STICKY_NOTE:
                        StickyNote stickyNote = (StickyNote) createObject.getBoardObject();
                        // Need to define custom exception: if meeting null?
                        Meeting meeting = findMeetingByUserId(existingUser.getUserId());
                        if(meeting == null) {System.err.println("[*] Error: User is not in any meeting."); break;}
                        int messageIdAck = createObject.getMessageId()+1;
                        if(!meeting.getWhiteboard().coordinatesAreBusy(stickyNote)) {
                            String serializedStickyNote = objectSerialize(stickyNote);
                            String sha256hash = generateSha256Hash(serializedStickyNote);
                            meeting.getWhiteboard().getBoardObjects().add(stickyNote);
                            ObjectCreated objectCreated = new ObjectCreated(messageIdAck, sha256hash);
                            System.out.println("[*] StickyNote created and added to meeting " + meeting.getMeetingId());
                            sendMessage(conn, objectCreated, MessageType.OBJECT_CREATED);
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
                if(meetings.size() <= 5) {
                    // 1st step: create new user, who will be the host of the meeting, and add it to server
                    User host = new User(idGenerator(IdType.USER_ID), createMeeting.getPseudo());
                    users.put(conn, host);
                    // 2nd step: create new meeting and add it to the server's hosted meetings
                    Meeting meeting = new Meeting(idGenerator(IdType.MEETING_ID), host);
                    meetings.add(meeting);
                    System.out.println("[*] New meeting created: " + meeting.getMeetingId());
                    // 3rd step: send back to the host a confirmation message
                    MeetingCreated meetingCreated = new MeetingCreated(messageIdAck, meeting.getMeetingId(), host.getUserId());
                    sendMessage(conn, meetingCreated, MessageType.MEETING_CREATED);
                } else { ServerFullError error = new ServerFullError(messageIdAck); }
                break;
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

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
        }
        return toString.toString();
    }

    @Override
    public void onStart() {
        System.out.println("Starting the server...");
    }

    private void broadcastMessage(Message message) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String messageJson = mapper.writeValueAsString(message);
            for (WebSocket sock : conns) {
                sock.send(messageJson);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Cannot convert message to json.");
        }
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
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method generates an 8-chars long string that represents a user ID.
     * @return userId: The newly generated user ID
     */
    private String idGenerator(IdType idType) {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength;
        switch(idType) {
            case USER_ID -> targetStringLength = 8;
            case MEETING_ID -> targetStringLength = 16;
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
            for(User user : meeting.getUsers()) {
                if(user.getUserId().equals(userId)) {
                    return meeting;
                }
            }
        }
        return null;
    }


    /*
    public int generateMessageId () {
        byte[] macAddress = getMacAddress();
        byte[] toMerge = new byte[2];
        Random random = new Random();
        random.nextBytes(toMerge);
        byte[] c = new byte[macAddress.length + toMerge.length];
        System.arraycopy(macAddress, 0, c, 0, macAddress.length);
        System.arraycopy(toMerge, 0, c, macAddress.length, toMerge.length);
        return ByteBuffer.wrap(c).getInt();
    }
     */

    /**
     * This function returns the MAC address of the server as an array of bytes.
     * @return
     */
    private byte[] getMacAddress() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            return ni.getHardwareAddress();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
