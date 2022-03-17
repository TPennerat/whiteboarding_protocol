package fi.whiteboardaalto;

import fi.whiteboardaalto.objects.BoardObject;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Meeting {
    private String meetingId;
    private Set<User> users;
    private User host;
    private Whiteboard whiteboard;

    public Meeting(String meetingId, User host) {
        this.meetingId = meetingId;
        this.users = new HashSet<User>();
        this.host = host;
        this.whiteboard = new Whiteboard();
    }

    public String getMeetingId() {
        return meetingId;
    }

    public Set<User> getUsers() {
        return users;
    }

    public User getHost() {
        return host;
    }

    public Whiteboard getWhiteboard() {
        return whiteboard;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public void setHost(User host) {
        this.host = host;
    }

    public void setWhiteboard(Whiteboard whiteboard) {
        this.whiteboard = whiteboard;
    }

    public boolean pseudoAlreadyExists(String testPseudo) {
        for (User user : users) {
            if(user.getPseudo().equals(testPseudo)) {
                return true;
            }
        }
        return false;
    }

    public void transferHost() {
        int size = users.size();
        int item = new Random().nextInt(size);
        int i = 0;
        User newHost = null;
        for(User user : users)
        {
            if (i == item)
                newHost = user;
            i++;
        }
        setHost(newHost);
    }

    @Override
    public String toString() {
        StringBuilder toString;
        toString = new StringBuilder("[*] Current objects in the meeting ").append(this.meetingId).append(" :");
        toString.append(System.lineSeparator());
        for(BoardObject boardObject : this.getWhiteboard().getBoardObjects()) {
            toString.append("> ").append(boardObject.getClass().getSimpleName()).append(", ID: ").append(boardObject.getObjectId());
            toString.append(System.lineSeparator());
        }
        return toString.toString();
    }

}
