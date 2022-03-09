package fi.whiteboardaalto;

import java.io.Serializable;

public class User implements Serializable {
    private String userId;
    private String pseudo;

    public User(String userId, String pseudo) {
        this.userId = userId;
        this.pseudo = pseudo;
    }

    public String getUserId() {
        return userId;
    }

    public String getPseudo() {
        return pseudo;
    }
}
