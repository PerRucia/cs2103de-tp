package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestLibrarian {
    private Librarian librarian;
    private BookList bookList;

    @BeforeEach
    void initFields() {
        bookList = new BookList();
        librarian = new Librarian("U1", "Alice", "alice@example.com",
                "Secret123", "E001", bookList);
    }

    /**
     * Test the constructor of the Librarian class.
     * This test checks if the constructor initializes all fields correctly.
     */
    @Test
    void testLibrarianConstructor() {
        Assertions.assertEquals("E001", librarian.getEmployeeId());

        // Test setting of employeeId
        librarian.setEmployeeId("E002");
        Assertions.assertEquals("E002", librarian.getEmployeeId());
    }

    /**
     * Test the addBook method of the Librarian class.
     * This test checks if the addBook method sets the book status to AVAILABLE.
     */
    @Test
    void testAddBook() {
        Book book = new Book("1234567890", "Effective Java", "Joshua Bloch");
        book.setStatus(BookStatus.OUT_OF_CIRCULATION);

        // Try adding a valid book
        librarian.addBook(book);
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());

        // Try adding a null book
        librarian.addBook(null);

        // Try adding a book with null ISBN
        Book invalidBook = new Book(null, "Java Concurrency in Practice",
                "Brian Goetz");
        librarian.addBook(invalidBook);
    }

    /**
     * Test the removeBook method of the Librarian class.
     * This test checks if the removeBook method sets the book status to UNAVAILABLE.
     * It also checks if the method prevents removing a book that is checked out or overdue.
     */
    @Test
    void testRemoveBook() {
        // Try removing an available book
        Book book = new Book("1234567890", "Effective Java", "Joshua Bloch");
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());
        librarian.removeBook(book);
        Assertions.assertEquals(BookStatus.OUT_OF_CIRCULATION, book.getStatus());

        // Try removing a checked-out book
        book.setStatus(BookStatus.CHECKED_OUT);
        librarian.removeBook(book);
        Assertions.assertEquals(BookStatus.CHECKED_OUT, book.getStatus());

        // Try removing an overdue book
        book.setStatus(BookStatus.OVERDUE);
        librarian.removeBook(book);
        Assertions.assertEquals(BookStatus.OVERDUE, book.getStatus());

        // Try removing a null book
        librarian.removeBook(null);
    }

    /**
     * Test the manageUsers method of the Librarian class.
     * This test checks if the manageUsers method executes without error.
     */
    @Test
    void manageUsersExecutesWithoutError() {
        Assertions.assertDoesNotThrow(librarian::manageUsers);
    }

    /**
     * Test the toString method of the Librarian class.
     * This test checks if the toString method returns a string representation of the librarian.
     */
    @Test
    void toStringContainsRelevantLibrarianInformation() {
        String librarianString = librarian.toString();
        Assertions.assertTrue(librarianString.contains("Alice"));
        Assertions.assertTrue(librarianString.contains("alice@example.com"));
        Assertions.assertTrue(librarianString.contains("E001"));
        Assertions.assertFalse(librarianString.contains("Secret123"));
    }
}