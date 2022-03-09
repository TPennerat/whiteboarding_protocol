package fi.whiteboardaalto;

import com.fasterxml.jackson.databind.JsonNode;
import fi.whiteboardaalto.messages.MessageType;
import fi.whiteboardaalto.messages.SuperMessage;
import fi.whiteboardaalto.messages.client.action.CreateObject;
import fi.whiteboardaalto.messages.client.session.CreateMeeting;
import fi.whiteboardaalto.messages.client.session.JoinMeeting;
import fi.whiteboardaalto.messages.client.session.LeaveMeeting;
import fi.whiteboardaalto.messages.server.ack.MeetingCreated;
import fi.whiteboardaalto.messages.server.ack.MeetingJoined;
import fi.whiteboardaalto.messages.server.ack.MeetingLeft;
import fi.whiteboardaalto.messages.server.errors.NonExistentMeeting;
import fi.whiteboardaalto.messages.server.errors.WrongFormatError;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
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
        System.out.println(toString());
        // Remove the user (to code)
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress() + ":" + conn.getRemoteSocketAddress().getPort());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        try {
            SuperMessage msg = mapper.readValue(message, SuperMessage.class);
            System.out.println("Message type is: " + msg.getType() + " and message payload is: " + msg.getObject());
            String messageString = msg.getObject().toString();
            // For each case of the switch, errors still need to be considered and handled through exceptions
            switch (msg.getType()) {
                case CREATE_MEETING:
                    CreateMeeting createMeeting = mapper.readValue(messageString, CreateMeeting.class);
                    users.put(conn, new User(idGenerator(IdType.USER_ID), createMeeting.getPseudo()));
                    // Preparing meeting creation message
                    MeetingCreated meetingCreated = new MeetingCreated(createMeeting.getMessageId()+1, idGenerator(IdType.MEETING_ID), users.get(conn).getUserId());
                    Meeting meeting = new Meeting(meetingCreated.getMeetingId(), users.get(conn));
                    meeting.setHost(users.get(conn));
                    meetings.add(meeting);
                    // Sending confirmation for meeting creation
                    sendMessage(conn, meetingCreated, MessageType.MEETING_CREATED);
                    // For debug purposes
                    System.out.println(toString());
                    break;
                case LEAVE_MEETING:
                    LeaveMeeting leaveMeeting = mapper.readValue(messageString, LeaveMeeting.class);
                    Meeting meetingToFind = findMeeting(leaveMeeting.getMeetingId());
                    String pseudo = users.get(conn).getPseudo();
                    MeetingLeft meetingLeft = new MeetingLeft(leaveMeeting.getMessageId()+1, meetingToFind.getMeetingId(), pseudo);
                    meetingToFind.getUsers().remove(users.get(conn));
                    sendMessage(conn, meetingLeft, MessageType.MEETING_LEFT);
                    System.out.println(pseudo + " has left meeting " + meetingToFind.getMeetingId() + ".");
                    conn.close();
                    break;
                case JOIN_MEETING:
                    JoinMeeting joinMeeting = mapper.readValue(messageString, JoinMeeting.class);
                    Meeting meetingToJoin = findMeeting(joinMeeting.getMeetingId());
                    if (meetingToJoin != null) {
                        System.out.println("Someone wants to join the following meeting: " + meetingToJoin.getMeetingId());
                        User newUser = new User(idGenerator(IdType.USER_ID), joinMeeting.getPseudo());
                        // Adding the user to the current list of users
                        users.put(conn, newUser);
                        MeetingJoined meetingJoined = new MeetingJoined(joinMeeting.getMessageId()+1, meetingToJoin.getMeetingId(), newUser.getUserId());
                        // Adding the new player
                        meetingToJoin.getUsers().add(newUser);
                        sendMessage(conn, meetingJoined, MessageType.MEETING_JOINED);
                        // For debug purposes
                        System.out.println(toString());
                    } else {
                        NonExistentMeeting error = new NonExistentMeeting(0x202, "This meeting doesn't exist");
                        sendMessage(conn, error, MessageType.NON_EXISTING_MEETING_ERROR);
                        System.out.println("Non existent meeting exception sent.");
                    }
                    break;
                case CREATE_OBJECT:
                    CreateObject createObject = mapper.readValue(messageString, CreateObject.class);

                    break;
            }
        } catch (JsonProcessingException e) {
            System.out.println("Wrong message format: " + e);
            WrongFormatError error = new WrongFormatError(0x201, "Message malformed.");
            sendMessage(conn, error, MessageType.WRONG_FORMAT_ERROR);
        }

        /*
        try {
            Message msg = mapper.readValue(message, Message.class);
            String type = msg.getType();
            System.out.println("Message received of type: " + type);
        } catch (JsonProcessingException e) {
            System.out.println("Wrong message format: " + e);
        }
         */
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

    /**
     * This method broadcasts a message to all the connected users.
     * @param msg
     */
    private void broadcastMessage(SuperMessage msg) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String messageJson = mapper.writeValueAsString(msg);
            for (WebSocket sock : conns) {
                sock.send(messageJson);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Cannot convert message to json.");
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

    /**
     * This method goes through all the meetings hosted by the server and returns a reference on the meeting
     * object for which the meeting ID is the same than the one provided in the parameters.s
     * @param meetingId
     * @return meeting: a reference on the found meeting; null if nothing was found.
     */
    private Meeting findMeeting(String meetingId) {
        for (Meeting meeting : meetings) {
            if(meeting.getMeetingId().equals(meetingId)) {
                return meeting;
            }
        }
        return null;
    }

    /**
     * This method is called when a JSON message needs to be sent over a WebSocket to a client.
     * @param conn
     * @param object
     */
    private void sendMessage(WebSocket conn, Object object, MessageType type) {
        try {
            JsonNode node = mapper.readTree(mapper.writeValueAsString(object));
            SuperMessage msg = new SuperMessage(type, node);
            conn.send(mapper.writeValueAsString(msg));

            if(object.getClass() == NonExistentMeeting.class) {
                conn.close();
            }
        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
        }
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
