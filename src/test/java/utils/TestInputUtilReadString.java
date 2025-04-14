package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestInputUtilReadString {
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
    void testReadString() {
        // Set up mock input
        String input = "Test Input\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        
        // Call the method being tested
        String result = InputUtil.readString("Enter text: ");
        
        // Verify the prompt message
        assertEquals("Enter text: ", outContent.toString());
        
        // Verify the return value
        assertEquals("Test Input", result);
    }
} 