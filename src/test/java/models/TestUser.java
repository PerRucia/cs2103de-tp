package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUser {
    private User user;

    @BeforeEach
    void initUser() {
        user = new User("U1", "Alice", "alice@example.com",
                "Secret123");
    }

    /**
     * Test the constructor of the User class.
     */
    @Test
    void testUserConstructor() {
        Assertions.assertEquals("U1", user.getUserId());
        Assertions.assertEquals("Alice", user.getName());
        Assertions.assertEquals("alice@example.com", user.getEmail());
    }

    /**
     * Test updating user values.
     * This test checks if the setters work correctly.
     */
    @Test
    void testUpdateUserValues() {
        // Check initial values
        Assertions.assertEquals("U1", user.getUserId());
        Assertions.assertEquals("Alice", user.getName());
        Assertions.assertEquals("alice@example.com", user.getEmail());

        // Update user values
        user.setUserId("U2");
        user.setName("Bob");
        user.setEmail("bob@example.com");

        // Check updated values
        Assertions.assertEquals("U2", user.getUserId());
        Assertions.assertEquals("Bob", user.getName());
        Assertions.assertEquals("bob@example.com", user.getEmail());
    }

    /**
     * Test the toString method of the User class.
     * This test checks if the toString method does not expose sensitive information.
     */
    @Test
    void testPasswordSecurity() {
        String userString = user.toString();
        Assertions.assertFalse(userString.contains("Secret123"));
        Assertions.assertTrue(userString.contains("U1"));
        Assertions.assertTrue(userString.contains("Alice"));
        Assertions.assertTrue(userString.contains("alice@example.com"));
    }

    /**
     * Test the authenticate method of the User class.
     * This test checks if the authentication works correctly.
     */
    @Test
    void testPasswordAuth() {
        Assertions.assertTrue(user.authenticate("Secret123"));
        Assertions.assertFalse(user.authenticate("Secret1234"));
    }
}