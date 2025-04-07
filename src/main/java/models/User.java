package models;

import java.io.Serializable;

/**
 * Represents a user in the library system.
 */
public class User implements Serializable {
    private String id;
    private boolean isAdmin;

    public User(String id, boolean isAdmin) {
        this.id = id;
        this.isAdmin = isAdmin;
    }

    public String getId() {
        return id;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", isAdmin=" + isAdmin +
                '}';
    }
}
