package fi.whiteboardaalto;

import java.util.HashSet;
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

}
