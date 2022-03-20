package fi.whiteboardaalto;

import java.io.Serializable;
import java.util.Objects;

public class User implements Serializable {
    private String userId;
    private String pseudo;

    public User(String userId, String pseudo) {
        this.userId = userId;
        this.pseudo = pseudo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && Objects.equals(pseudo, user.pseudo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, pseudo);
    }

    public String getUserId() {
        return userId;
    }

    public String getPseudo() {
        return pseudo;
    }
}
