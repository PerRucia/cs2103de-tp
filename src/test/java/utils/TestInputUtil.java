package utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

public class TestInputUtil {
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
    void testInputUtil() {
        // Test readString
        {
            // Set up mock input
            String input = "Test Input\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            // Call the method being tested
            String result = InputUtil.readString("Enter text: ");
            
            // Verify the prompt message
            assertEquals("Enter text: ", outContent.toString());
            
            // Verify the return value
            assertEquals("Test Input", result);
            
            // Reset output buffer
            outContent.reset();
        }
        
        // Test readInt with valid input
        {
            // Set up mock input
            String input = "42\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            // Call the method being tested
            int result = InputUtil.readInt("Enter number: ");
            
            // Verify the prompt message
            assertEquals("Enter number: ", outContent.toString());
            
            // Verify the return value
            assertEquals(42, result);
            
            // Reset output buffer
            outContent.reset();
        }
        
        // Test readYesNo with "y" input
        {
            // Set up mock input
            String input = "y\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            // Call the method being tested
            boolean result = InputUtil.readYesNo("Continue? (y/n): ");
            
            // Verify the prompt message
            assertEquals("Continue? (y/n): ", outContent.toString());
            
            // Verify the return value
            assertTrue(result);
            
            // Reset output buffer
            outContent.reset();
        }
        
        // Test readYesNo with "n" input
        {
            // Set up mock input
            String input = "n\n";
            System.setIn(new ByteArrayInputStream(input.getBytes()));
            
            // Call the method being tested
            boolean result = InputUtil.readYesNo("Continue? (y/n): ");
            
            // Verify the prompt message
            assertEquals("Continue? (y/n): ", outContent.toString());
            
            // Verify the return value
            assertFalse(result);
        }
    }
} 