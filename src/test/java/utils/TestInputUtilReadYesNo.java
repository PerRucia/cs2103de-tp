package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestInputUtilReadYesNo {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @BeforeEach
    void setUp() {
        // Redirect standard output for testing
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        // Restore standard input and output
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testReadYesNoWithY() {
        // Set up mock input
        String input = "y\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method being tested
        boolean result = InputUtil.readYesNo("Continue? (y/n): ");
        
        // Verify the prompt message
        assertEquals("Continue? (y/n): ", outContent.toString());
        
        // Verify the return value
        assertTrue(result);
    }
} 