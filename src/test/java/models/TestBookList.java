package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestBookList {
    private BookList bookList;
    private Book book;

    @BeforeEach
    void initFields() {
        bookList = new BookList();
        book = new Book("1234567890", "Effective Java", "Joshua Bloch");
        bookList.addBook(book);
    }

    /**
     * Test the constructor of the BookList class.
     * This test checks if the constructor initializes an empty book list.
     */
    @Test
    void testAddBook() {
        Assertions.assertTrue(bookList.containsBook("1234567890"));
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());
        Assertions.assertEquals(book, bookList.getBook("1234567890"));

        // Test adding a null book
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookList.addBook(null));

        // Test adding a book with null ISBN
        book = new Book(null, "Java Concurrency in Practice", "Brian Goetz");
        Assertions.assertThrows(IllegalArgumentException.class, () -> bookList.addBook(book));
    }

    /**
     * Test the removeBook method of the BookList class.
     * This test checks if the removeBook method sets the book status to OUT_OF_CIRCULATION.
     */
    @Test
    void testRemoveBook() {
        // Remove a valid book
        bookList.removeBook(book);
        Assertions.assertFalse(bookList.containsBook("1234567890"));
        Assertions.assertEquals(BookStatus.OUT_OF_CIRCULATION, book.getStatus());

        // Remove a checked-out book
        bookList.addBook(book);
        book.setStatus(BookStatus.CHECKED_OUT);
        Assertions.assertThrows(IllegalStateException.class, () -> bookList.removeBook(book));

        // Remove an unavailable book
        book.setStatus(BookStatus.OUT_OF_CIRCULATION);
        Assertions.assertThrows(IllegalStateException.class, () -> bookList.removeBook(book));

        // Remove an overdue book
        book.setStatus(BookStatus.OVERDUE);
        Assertions.assertThrows(IllegalStateException.class, () -> bookList.removeBook(book));
    }

    /**
     * Test the returnBook method of the BookList class.
     * This test checks if the returnBook method sets the book status to AVAILABLE.
     */
    @Test
    void testLoanBook() {
        // Loan a valid book
        bookList.loanBook(book);
        Assertions.assertEquals(BookStatus.CHECKED_OUT, book.getStatus());

        // Loan an unavailable book
        book.setStatus(BookStatus.OUT_OF_CIRCULATION);
        Assertions.assertThrows(IllegalStateException.class, () -> bookList.loanBook(book));

        // Loan an overdue book
        book.setStatus(BookStatus.OVERDUE);
        Assertions.assertThrows(IllegalStateException.class, () -> bookList.loanBook(book));
    }

    /**
     * Test the returnBook method of the BookList class.
     * This test checks if the returnBook method sets the book status to AVAILABLE.
     */
    @Test
    void testReturnBook() {
        // Return a valid book
        book.setStatus(BookStatus.CHECKED_OUT);
        bookList.returnBook(book);
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());

        // Return an unavailable book
        book.setStatus(BookStatus.OUT_OF_CIRCULATION);
        Assertions.assertThrows(IllegalStateException.class, () -> bookList.returnBook(book));

        // Return an overdue book
        book.setStatus(BookStatus.OVERDUE);
        bookList.returnBook(book);
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }

    /**
     * Test the overdueBook method of the BookList class.
     * This test checks if the overdueBook method sets the book status to OVERDUE.
     */
    @Test
    void testOverdueBook() {
        // Mark a valid book as overdue
        book.setStatus(BookStatus.CHECKED_OUT);
        bookList.overdueBook(book);
        Assertions.assertEquals(BookStatus.OVERDUE, book.getStatus());

        // Mark an unavailable book as overdue
        book.setStatus(BookStatus.OUT_OF_CIRCULATION);
        Assertions.assertThrows(IllegalStateException.class, () -> bookList.overdueBook(book));

        // Mark an overdue book as overdue again
        Assertions.assertThrows(IllegalStateException.class, () -> bookList.overdueBook(book));
    }

    /**
     * Test the getBook method of the BookList class.
     * This test checks if the getBook method returns the correct book.
     */
    @Test
    void testGetBookNotFound() {
        Assertions.assertNull(bookList.getBook("nonexistent"));
    }

    /**
     * Test the getAllBooks method of the BookList class.
     * This test checks if the getAllBooks method returns a copy of the book list.
     */
    @Test
    void testGetAllBooksIndependence() {
        Book book1 = new Book("1234567890", "Effective Java", "Joshua Bloch");
        Book book2 = new Book("0987654321", "Java Concurrency in Practice", "Brian Goetz");
        bookList.addBook(book1);
        bookList.addBook(book2);
        var booksCopy = bookList.getAllBooks();
        Assertions.assertEquals(2, booksCopy.size());
        booksCopy.remove("1234567890");
        Assertions.assertEquals(2, bookList.getAllBooks().size());
    }
}