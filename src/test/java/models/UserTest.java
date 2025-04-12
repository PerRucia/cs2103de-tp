package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Test
    public void testUserAccessors() {
        // Test with admin true
        User userAdmin = new User("adminId", true);
        assertEquals("adminId", userAdmin.getId());
        assertTrue(userAdmin.isAdmin());

        // Test with admin false
        User userNonAdmin = new User("userId", false);
        assertEquals("userId", userNonAdmin.getId());
        assertFalse(userNonAdmin.isAdmin());
    }

    @Test
    public void testUserToString() {
        User user = new User("testId", false);
        String expected = "User{" + "id='testId'" + ", isAdmin=false" + '}';
        assertEquals(expected, user.toString());
    }
}
