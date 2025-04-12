package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;

public class BookListTest {

    private BookList bookList;
    private Book book1, book2, book3;

    @BeforeEach
    public void setUp() {
        bookList = new BookList();
        book1 = new Book("ISBN001", "Alpha", "AuthorA");
        book2 = new Book("ISBN002", "Bravo", "AuthorB");
        book3 = new Book("ISBN003", "Charlie", "AuthorC");
    }

    @Test
    public void testAddBook() {
        bookList.addBook(book1);
        assertEquals(BookStatus.AVAILABLE, book1.getStatus());
        assertTrue(bookList.containsBook("ISBN001"));
    }

    @Test
    public void testAddBookInvalid() {
        Book invalidBook = new Book(null, "NoISBN", "AuthorX");
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookList.addBook(invalidBook);
        });
        assertEquals("Book or ISBN cannot be null or empty.", exception.getMessage());
    }

    @Test
    public void testRemoveBook() {
        bookList.addBook(book1);
        bookList.removeBook(book1);
        assertEquals(BookStatus.OUT_OF_CIRCULATION, book1.getStatus());
        assertFalse(bookList.containsBook("ISBN001"));
    }

    @Test
    public void testRemoveBookInvalid() {
        bookList.addBook(book1);
        bookList.loanBook(book1);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookList.removeBook(book1);
        });
        assertEquals("Cannot remove book that is not available.", exception.getMessage());
    }

    @Test
    public void testLoanBook() {
        bookList.addBook(book2);
        bookList.loanBook(book2);
        assertEquals(BookStatus.CHECKED_OUT, book2.getStatus());
    }

    @Test
    public void testLoanBookInvalid() {
        bookList.addBook(book2);
        bookList.loanBook(book2);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookList.loanBook(book2);
        });
        assertEquals("Cannot loan a book that is not available.", exception.getMessage());
    }

    @Test
    public void testReturnBook() {
        bookList.addBook(book3);
        bookList.loanBook(book3);
        bookList.returnBook(book3);
        assertEquals(BookStatus.AVAILABLE, book3.getStatus());
    }

    @Test
    public void testReturnBookInvalid() {
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookList.returnBook(book3);
        });
        assertEquals("Cannot return a book that is not checked out.", exception.getMessage());
    }

    @Test
    public void testOverdueBook() {
        bookList.addBook(book1);
        bookList.loanBook(book1);
        bookList.overdueBook(book1);
        assertEquals(BookStatus.OVERDUE, book1.getStatus());
    }

    @Test
    public void testOverdueBookInvalid() {
        bookList.addBook(book2);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            bookList.overdueBook(book2);
        });
        assertEquals("Cannot mark a book as overdue that is not checked out.", exception.getMessage());
    }

    @Test
    public void testGetSortedBooks() {
        // Add books for initial sorting tests
        bookList.addBook(book1);
        bookList.addBook(book2);
        bookList.addBook(book3);

        // Test sorting by TITLE ascending and descending
        var sortedTitleAsc = bookList.getSortedBooks(SortCriteria.TITLE, true);
        assertEquals("Alpha", sortedTitleAsc.get(0).getTitle());
        var sortedTitleDesc = bookList.getSortedBooks(SortCriteria.TITLE, false);
        assertEquals("Charlie", sortedTitleDesc.get(0).getTitle());

        // Test sorting by AUTHOR ascending and descending
        var sortedAuthorAsc = bookList.getSortedBooks(SortCriteria.AUTHOR, true);
        assertEquals("AuthorA", sortedAuthorAsc.get(0).getAuthor());
        var sortedAuthorDesc = bookList.getSortedBooks(SortCriteria.AUTHOR, false);
        assertEquals("AuthorC", sortedAuthorDesc.get(0).getAuthor());

        // Test sorting by ISBN ascending and descending
        var sortedIsbnAsc = bookList.getSortedBooks(SortCriteria.ISBN, true);
        assertEquals("ISBN001", sortedIsbnAsc.get(0).getIsbn());
        var sortedIsbnDesc = bookList.getSortedBooks(SortCriteria.ISBN, false);
        assertEquals("ISBN003", sortedIsbnDesc.get(0).getIsbn());

        // Test sorting by STATUS: Clear and re-add books with distinct statuses
        bookList.clear();
        bookList.addBook(book1); // status: AVAILABLE
        bookList.addBook(book2); // status: AVAILABLE -> will loan to become CHECKED_OUT
        bookList.addBook(book3); // status: AVAILABLE -> will set to OVERDUE

        // Update statuses for testing STATUS sorting
        bookList.loanBook(book2);              // book2 -> CHECKED_OUT
        bookList.loanBook(book3);
        bookList.overdueBook(book3);           // book3 -> OVERDUE

        // Sorting by STATUS ascending: expected alphabetical order "AVAILABLE", "CHECKED_OUT", "OVERDUE"
        var sortedStatusAsc = bookList.getSortedBooks(SortCriteria.STATUS, true);
        assertEquals(BookStatus.AVAILABLE, sortedStatusAsc.get(0).getStatus());
        // Sorting by STATUS descending
        var sortedStatusDesc = bookList.getSortedBooks(SortCriteria.STATUS, false);
        assertEquals(BookStatus.OVERDUE, sortedStatusDesc.get(0).getStatus());
    }

    @Test
    public void testSearchBooks() {
        bookList.addBook(book1);
        bookList.addBook(book2);
        var results = bookList.searchBooks("brav", SearchCriteria.TITLE);
        assertEquals(1, results.size());
        assertEquals("Bravo", results.get(0).getTitle());
    }

    @Test
    public void testSearchAndSortBooks() {
        bookList.addBook(book1);
        bookList.addBook(book2);
        bookList.addBook(book3);
        var results = bookList.searchAndSortBooks("a", SearchCriteria.ALL, SortCriteria.AUTHOR);
        assertFalse(results.isEmpty());

        // Check if the results are sorted by author
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getAuthor().compareTo(results.get(i + 1).getAuthor()) <= 0);
        }

        // Sort by ISBN
        results = bookList.searchAndSortBooks("a", SearchCriteria.ALL, SortCriteria.ISBN);
        assertFalse(results.isEmpty());

        // Check if the results are sorted by ISBN
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getIsbn().compareTo(results.get(i + 1).getIsbn()) <= 0);
        }

        // Sort by title
        results = bookList.searchAndSortBooks("a", SearchCriteria.ALL, SortCriteria.TITLE);
        assertFalse(results.isEmpty());

        // Check if the results are sorted by title
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getTitle().compareTo(results.get(i + 1).getTitle()) <= 0);
        }

        // Sort by status
        results = bookList.searchAndSortBooks("a", SearchCriteria.ALL, SortCriteria.STATUS);
        assertFalse(results.isEmpty());

        // Check if the results are sorted by status
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getStatus().compareTo(results.get(i + 1).getStatus()) <= 0);
        }
    }

    @Test
    public void testSearchBooksByRelevance() {
        bookList.addBook(book1);
        bookList.addBook(book2);
        bookList.addBook(book3);
        var results = bookList.searchBooksByRelevance("char", SearchCriteria.TITLE);
        assertEquals(1, results.size());
        assertEquals("Charlie", results.get(0).getTitle());
    }

    @Test
    public void testClear() {
        bookList.addBook(book1);
        bookList.addBook(book2);
        bookList.clear();
        assertTrue(bookList.getBooks().isEmpty());
    }

    @Test
    public void testGetBook() {
        bookList.addBook(book1);
        bookList.addBook(book2);

        Book retrievedBook = bookList.getBook("ISBN001");
        assertNotNull(retrievedBook);
    }
}