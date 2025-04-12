package models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class BookTest {

    @Test
    public void testConstructorAndGetters() {
        Book book = new Book("1234567890", "Test Title", "Test Author");
        assertEquals("1234567890", book.getIsbn());
        assertEquals("Test Title", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }

    @Test
    public void testSetters() {
        Book book = new Book("1234567890", "Test Title", "Test Author");
        book.setIsbn("0987654321");
        book.setTitle("New Title");
        book.setAuthor("New Author");
        book.setStatus(BookStatus.CHECKED_OUT);
        
        assertEquals("0987654321", book.getIsbn());
        assertEquals("New Title", book.getTitle());
        assertEquals("New Author", book.getAuthor());
        assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
    }

    @Test
    public void testToStringFormat() {
        Book book = new Book("1234567890", "Test Title", "Test Author");
        String expected = String.format("ISBN: %s | Title: %s | Author: %s | Status: %s",
                                        "1234567890", "Test Title", "Test Author", BookStatus.AVAILABLE);
        assertEquals(expected, book.toString());
    }
}
