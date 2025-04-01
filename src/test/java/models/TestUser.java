package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUser {
    private User regularUser;
    private User adminUser;

    @BeforeEach
    void initUser() {
        regularUser = new User(false);
        adminUser = new User(true);
    }

    /**
     * Test the constructor of the User class.
     * This test checks if the constructor initializes the isAdmin field correctly.
     */
    @Test
    void testUserConstructor() {
        Assertions.assertFalse(regularUser.isAdmin());
        Assertions.assertTrue(adminUser.isAdmin());
    }

    /**
     * Test the toString method of the User class.
     * This test checks if the toString method returns a string representation of the user.
     */
    @Test
    void testToString() {
        String regularUserString = regularUser.toString();
        String adminUserString = adminUser.toString();
        
        Assertions.assertTrue(regularUserString.contains("isAdmin=false"));
        Assertions.assertTrue(adminUserString.contains("isAdmin=true"));
    }
}