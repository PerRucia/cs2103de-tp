package utils.authentication;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;

public class TestUserAuth {

    private static final String TEST_USERS_FILE = "data/test_users.txt";
    private static Path backupPath = null;
    private static final UserAuth userAuth = new UserAuth(TEST_USERS_FILE);

    @BeforeAll
    public static void setup() throws IOException {
        // Create data directory if it doesn't exist
        Files.createDirectories(Paths.get("data"));

        // Backup existing users file if it exists
        Path originalPath = Paths.get(TEST_USERS_FILE);
        if (Files.exists(originalPath)) {
            backupPath = Paths.get(TEST_USERS_FILE + ".bak");
            Files.move(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @AfterAll
    public static void cleanup() throws IOException {
        // Restore backup if it exists
        if (backupPath != null && Files.exists(backupPath)) {
            Files.move(backupPath, Paths.get(TEST_USERS_FILE), StandardCopyOption.REPLACE_EXISTING);
        } else {
            // Delete test file if no backup
            Files.deleteIfExists(Paths.get(TEST_USERS_FILE));
        }
    }

    @BeforeEach
    public void clearUsersFile() throws IOException {
        // Clear the users file before each test
        new FileWriter(TEST_USERS_FILE).close();
    }

    /**
     * Test user registration and authentication.
     */
    @Test
    public void testRegisterAndAuthenticateUser() {
        Assertions.assertEquals("User registered successfully!",
                userAuth.registerUser("testUser", "testPassword"));

        // Check with correct credentials
        Assertions.assertTrue(userAuth.authenticateUser("testUser",
                "testPassword"));
        // Check with incorrect credentials
        Assertions.assertFalse(userAuth.authenticateUser("testUser",
                "wrongPassword"));
    }

    /**
     * Test multiple users.
     * Register multiple users and check if they can be authenticated.
     */
    @Test
    public void testMultipleUsers() {
        userAuth.registerUser("user1", "Password1");
        userAuth.registerUser("user2", "Password2");
        userAuth.registerUser("user3", "Password3");

        Assertions.assertTrue(userAuth.authenticateUser("user1", "Password1"));
        Assertions.assertTrue(userAuth.authenticateUser("user2", "Password2"));
        Assertions.assertTrue(userAuth.authenticateUser("user3", "Password3"));
    }

    /**
     * Test empty username and password.
     * Username and password cannot be null or empty.
     */
    @Test
    public void testEmptyCredentials() {
        // Test empty username
        String errorString = "Username cannot be null or empty.";

        Assertions.assertEquals(errorString,
                userAuth.registerUser("", "password"));
        Assertions.assertEquals(errorString,
                userAuth.registerUser(null, "password"));

        // Test empty password
        String errorString2 = "Password cannot be null or empty.";

        Assertions.assertEquals(errorString2,
                userAuth.registerUser("username", ""));
        Assertions.assertEquals(errorString2,
                userAuth.registerUser("username", null));
    }

    /**
     * Test invalid usernames.
     * Usernames must be unique and follow certain rules.
     */
    @Test
    public void testInvalidUsernames() {
        // Repeated username
        userAuth.registerUser("testUser", "testPassword");
        Assertions.assertEquals("Username already in use.",
                userAuth.registerUser("testUser", "newPassword"));

        // Username too short
        Assertions.assertEquals("Username must be at least 3 characters long.",
                userAuth.registerUser("ab", "password"));

        // Username too long
        Assertions.assertEquals("Username must be at most 20 characters long.",
                userAuth.registerUser("thisIsAVeryLongUsername", "password"));

        // Username contains invalid characters
        String errorString = "Username can only contain letters, digits, dots, " +
                "underscores, and hyphens.";
        Assertions.assertEquals(errorString,
                userAuth.registerUser("username%", "password"));
        Assertions.assertEquals(errorString,
                userAuth.registerUser(" username ", "password"));
        Assertions.assertEquals(errorString,
                userAuth.registerUser("username|", "password"));
    }

    /**
     * Test invalid passwords.
     * Passwords must follow certain rules.
     */
    @Test
    public void testInvalidPasswords() {
        // Password too short
        Assertions.assertEquals("Password must be at least 8 characters long.",
                userAuth.registerUser("username", "short"));

        // Password too long
        Assertions.assertEquals("Password must be at most 20 characters long.",
                userAuth.registerUser("username", "thisIsAVeryLongPassword"));

        // Password has no uppercase letter
        Assertions.assertEquals("Password must contain at least one uppercase letter.",
                userAuth.registerUser("username", "lowercasepassword"));

        // Password has no lowercase letter
        Assertions.assertEquals("Password must contain at least one lowercase letter.",
                userAuth.registerUser("username", "UPPERCASEPASSWORD"));

        // Password contains illegal characters
        String errorString = "Password cannot contain spaces or | character.";
        Assertions.assertEquals(errorString,
                userAuth.registerUser("username", "Password "));
        Assertions.assertEquals(errorString,
                userAuth.registerUser("username", "Password|"));
    }

    /**
     * Test authentication with non-existent user.
     * Attempt to authenticate a user that does not exist.
     */
    @Test
    public void testNonExistentUser() {
        Assertions.assertFalse(userAuth.authenticateUser("nonExistentUser", "password"));
    }

    /**
     * Test authentication with empty credentials.
     * Attempt to authenticate with empty username or password.
     */
    @Test
    public void caseSensitivityTest() {
        userAuth.registerUser("CaseSensitiveUser", "CaseSensitivePass");

        // Username should be case-sensitive
        Assertions.assertFalse(userAuth.authenticateUser("casesensitiveuser", "CaseSensitivePass"));
        // Password should be case-sensitive
        Assertions.assertFalse(userAuth.authenticateUser("CaseSensitiveUser", "casesensitivepass"));
    }

    /**
     * Simulate an IO error by creating a directory with the same name as the users file.
     * This should cause an IOException when trying to write to the file.
     * @throws IOException If an IO error occurs.
     */
    @Test
    public void testFileIOError() throws IOException {
        // Create a directory with the same name as the users file to cause an IO error
        File testDir = new File(TEST_USERS_FILE);
        testDir.delete(); // Remove existing file
        testDir.mkdir();  // Create directory with same name

        String result = userAuth.registerUser("testUser", "testPass");
        Assertions.assertEquals("Error registering user: data\\test_users.txt " +
                "(Access is denied)", result);
        Assertions.assertFalse(userAuth.authenticateUser("testUser", "testPass"));

        testDir.delete(); // Clean up
    }

    /**
     * Test multiple consecutive authentications.
     * Authenticate the same user multiple times in a row.
     */
    @Test
    public void testConsecutiveAuthentications() {
        userAuth.registerUser("userForMultipleAuths", "Password123");

        // Authenticate multiple times
        for (int i = 0; i < 5; i++) {
            Assertions.assertTrue(userAuth.authenticateUser("userForMultipleAuths",
                    "Password123"));
        }
    }
}