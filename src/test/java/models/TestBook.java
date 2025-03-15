package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestBook {
    private Book book;

    @BeforeEach
    void initBook() {
        book = new Book("1234567890", "Effective Java", "Joshua Bloch");
    }

    /**
     * Test the constructor of the Book class.
     * This test checks if the constructor initializes all fields correctly.
     */
    @Test
    void testBookConstructor() {
        Assertions.assertEquals("1234567890", book.getIsbn());
        Assertions.assertEquals("Effective Java", book.getTitle());
        Assertions.assertEquals("Joshua Bloch", book.getAuthor());
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }

    /**
     * Test updating book status.
     * This test checks if the setters work correctly.
     */
    @Test
    void testUpdateStatus() {
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());

        // Checked out
        book.setStatus(BookStatus.CHECKED_OUT);
        Assertions.assertEquals(BookStatus.CHECKED_OUT, book.getStatus());

        // Overdue
        book.setStatus(BookStatus.OVERDUE);
        Assertions.assertEquals(BookStatus.OVERDUE, book.getStatus());

        // Unavailable
        book.setStatus(BookStatus.OUT_OF_CIRCULATION);
        Assertions.assertEquals(BookStatus.OUT_OF_CIRCULATION, book.getStatus());
    }

    /**
     * Test the toString method of the Book class.
     */
    @Test
    void testToString() {
        book.setStatus(BookStatus.OVERDUE);
        String bookString = book.toString();
        Assertions.assertTrue(bookString.contains("1234567890"));
        Assertions.assertTrue(bookString.contains("Effective Java"));
        Assertions.assertTrue(bookString.contains("Joshua Bloch"));
        Assertions.assertTrue(bookString.contains("OVERDUE"));
    }

    /**
     * Test the getters and setters of the Book class.
     * This test checks if the getters and setters work correctly.
     */
    @Test
    void testSetMethods() {
        book.setIsbn("0987654321");
        book.setTitle("Java Concurrency in Practice");
        book.setAuthor("Brian Goetz");
        Assertions.assertEquals("0987654321", book.getIsbn());
        Assertions.assertEquals("Java Concurrency in Practice", book.getTitle());
        Assertions.assertEquals("Brian Goetz", book.getAuthor());
    }
}