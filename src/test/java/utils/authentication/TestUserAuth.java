package utils.authentication;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;

public class TestUserAuth {

    private static final String TEST_USERS_FILE = "data/test_users.txt";
    private static Path backupPath = null;

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

    @Test
    public void registerAndAuthenticateUser() {
        UserAuth.registerUser("testuser", "testpass");

        boolean result = UserAuth.authenticateUser("testuser", "testpass");

        Assertions.assertTrue(result);
    }

    @Test
    public void authenticateNonExistentUser() {
        boolean result = UserAuth.authenticateUser("nonexistent", "anypassword");

        Assertions.assertFalse(result);
    }

    @Test
    public void authenticateWithWrongPassword() {
        UserAuth.registerUser("user1", "correctpassword");

        boolean result = UserAuth.authenticateUser("user1", "wrongpassword");

        Assertions.assertFalse(result);
    }

    @Test
    public void registerMultipleUsers() {
        UserAuth.registerUser("user1", "pass1");
        UserAuth.registerUser("user2", "pass2");
        UserAuth.registerUser("user3", "pass3");

        Assertions.assertTrue(UserAuth.authenticateUser("user1", "pass1"));
        Assertions.assertTrue(UserAuth.authenticateUser("user2", "pass2"));
        Assertions.assertTrue(UserAuth.authenticateUser("user3", "pass3"));
    }

    @Test
    public void handleEmptyCredentials() {
        UserAuth.registerUser("", "");

        Assertions.assertFalse(UserAuth.authenticateUser("", ""));
    }

    @Test
    public void handleSpecialCharactersInCredentials() {
        String username = "special!@#$%^&*()";
        String password = "p@$$w0rd!";

        UserAuth.registerUser(username, password);

        Assertions.assertTrue(UserAuth.authenticateUser(username, password));
    }

    @Test
    public void caseSensitiveAuthentication() {
        UserAuth.registerUser("CaseSensitive", "Password");

        Assertions.assertTrue(UserAuth.authenticateUser("CaseSensitive", "Password"));
        Assertions.assertFalse(UserAuth.authenticateUser("casesensitive", "Password"));
        Assertions.assertFalse(UserAuth.authenticateUser("CaseSensitive", "password"));
    }

    @Test
    public void registerSameUsernameTwice() {
        UserAuth.registerUser("duplicate", "password1");
        UserAuth.registerUser("duplicate", "password2");

        Assertions.assertTrue(UserAuth.authenticateUser("duplicate", "password1"));
        Assertions.assertFalse(UserAuth.authenticateUser("duplicate", "password2"));
    }
}