package models;

import java.io.Serializable;

/**
 * Represents a user in the library system.
 */
public class User implements Serializable {
    private boolean isAdmin;

    public User(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    @Override
    public String toString() {
        return "User{" +
                "isAdmin=" + isAdmin +
                '}';
    }
}
