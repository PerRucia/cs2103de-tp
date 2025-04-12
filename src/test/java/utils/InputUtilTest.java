package utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class InputUtilTest {
    private final InputStream systemIn = System.in;

    // Helper to reset InputUtil's scanner with the current System.in
    private void resetScanner() throws Exception {
        Field scannerField = InputUtil.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        // Set a new Scanner using the updated System.in
        scannerField.set(null, new Scanner(System.in));
    }

    @BeforeEach
    public void setUp() throws Exception {
        // nothing to do here; tests will set System.in and reset scanner later
    }

    @AfterEach
    public void tearDown() throws Exception {
        System.setIn(systemIn);
    }
    
    @Test
    public void testReadString() throws Exception {
        String input = "hello\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        resetScanner();
        String result = InputUtil.readString("Enter string: ");
        assertEquals("hello", result);
    }
    
    @Test
    public void testReadInt_Valid() throws Exception {
        String input = "123\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        resetScanner();
        int result = InputUtil.readInt("Enter int: ");
        assertEquals(123, result);
    }
    
    @Test
    public void testReadInt_InvalidThenValid() throws Exception {
        // First input is invalid ("abc"), then valid ("456")
        String input = "abc\n456\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        resetScanner();
        int result = InputUtil.readInt("Enter int: ");
        assertEquals(456, result);
    }
    
    @Test
    public void testReadYesNo_Yes() throws Exception {
        String input = "yes\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        resetScanner();
        boolean result = InputUtil.readYesNo("Continue? ");
        assertTrue(result);
    }
    
    @Test
    public void testReadYesNo_Y() throws Exception {
        String input = "y\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        resetScanner();
        boolean result = InputUtil.readYesNo("Continue? ");
        assertTrue(result);
    }
    
    @Test
    public void testReadYesNo_No() throws Exception {
        String input = "no\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        resetScanner();
        boolean result = InputUtil.readYesNo("Continue? ");
        assertFalse(result);
    }
    
    // ...existing code if any...
}
