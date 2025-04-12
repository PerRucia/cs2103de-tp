package service;

import models.*;
import storage.GeneralStorage;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {
    private LibraryService libraryService;
    private MockedStatic<GeneralStorage> generalStorageMock;
    private BookList dummyBookList;
    private UserPreferences dummyPrefs;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setUp() {
        dummyBookList = new BookList();
        dummyPrefs = new UserPreferences();
        generalStorageMock = Mockito.mockStatic(GeneralStorage.class);
        generalStorageMock.when(() -> GeneralStorage.loadBookList(Mockito.anyString())).thenReturn(dummyBookList);
        generalStorageMock.when(() -> GeneralStorage.loadUserPreferences(Mockito.anyString())).thenReturn(dummyPrefs);

        libraryService = new LibraryService();

        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        generalStorageMock.close();
    }

    @Test
    void testSetAndGetCurrentUser() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        assertEquals(user, libraryService.getCurrentUser());
    }

    @Test
    void testAddBookSuccess() {
        libraryService.addBook("123", "Test Book", "Author A");
        assertNotNull(dummyBookList.getBook("123"));
        assertEquals("Test Book", dummyBookList.getBook("123").getTitle());
        assertTrue(outContent.toString().contains("Book added successfully"));
    }

    @Test
    void testAddBookFailure() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                libraryService.addBook("", "Test Book", "Author A")
        );
        assertTrue(exception.getMessage().contains("ISBN cannot be empty"));

        exception = assertThrows(IllegalArgumentException.class, () ->
                libraryService.addBook("123", "", "Author A")
        );

        assertTrue(exception.getMessage().contains("Title cannot be empty"));

        exception = assertThrows(IllegalArgumentException.class, () ->
                libraryService.addBook("123", "Test Book", "")
        );

        assertTrue(exception.getMessage().contains("Author cannot be empty"));
    }

    @Test
    void testRemoveBookSuccess() {
        libraryService.addBook("123", "Test Book", "Author A");
        libraryService.removeBook("123");
        assertNull(dummyBookList.getBook("123"));
        assertTrue(outContent.toString().contains("Book removed successfully."));
    }

    @Test
    void testRemoveBookFailureEmptyISBN() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                libraryService.removeBook("")
        );
        assertTrue(exception.getMessage().contains("ISBN cannot be empty"));
    }

    @Test
    void testRemoveBookFailureNonExisting() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                libraryService.removeBook("nonexistent")
        );
        assertTrue(exception.getMessage().contains("Book not found"));
    }

    @Test
    void testRemoveBookFailureCheckedOut() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Test Book", "Author A");
        libraryService.loanBook("123");
        Exception exception = assertThrows(IllegalStateException.class, () ->
                libraryService.removeBook("123")
        );
        assertTrue(exception.getMessage().contains("Cannot remove book because it is currently "));
    }

    @Test
    void testLoanAndReturnBook() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Test Book", "Author A");
        libraryService.loanBook("123");
        Book book = dummyBookList.getBook("123");
        assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
        List<Loan> currentLoans = libraryService.viewLoans();
        assertFalse(currentLoans.isEmpty());
        libraryService.returnBook("123");
        assertEquals(BookStatus.AVAILABLE, book.getStatus());
        Loan loan = currentLoans.get(0);
        assertTrue(loan.isReturned());
        assertNotNull(loan.getReturnDate());
        assertTrue(outContent.toString().contains("Book returned successfully."));
    }

    @Test
    void testLoanBookWithoutUser() {
        libraryService.addBook("123", "Test Book", "Author A");
        Exception exception = assertThrows(IllegalStateException.class, () ->
                libraryService.loanBook("123")
        );
        assertTrue(exception.getMessage().contains("No user is currently logged in."));
    }

    @Test
    void testLoanBookFailureIfNotAvailable() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Test Book", "Author A");
        libraryService.loanBook("123");
        Exception exception = assertThrows(IllegalStateException.class, () ->
                libraryService.loanBook("123")
        );
        assertTrue(exception.getMessage().contains("Book is not available for loan"));
    }

    @Test
    void testLoanBookFailureEmptyISBN() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                libraryService.loanBook("")
        );
        assertTrue(exception.getMessage().contains("ISBN cannot be empty"));
    }

    @Test
    void testGetMyLoans() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Test Book", "Author A");
        libraryService.loanBook("123");
        List<Loan> myLoans = libraryService.getMyLoans();
        assertEquals(1, myLoans.size());
        assertEquals("123", myLoans.get(0).getBook().getIsbn());
    }

    @Test
    void testViewLoansSorted() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Alpha", "Author A");
        libraryService.addBook("456", "Beta", "Author B");
        libraryService.loanBook("123");
        libraryService.loanBook("456");
        libraryService.viewLoansSorted(LoanSortCriteria.BOOK_TITLE, true, false);
        String output = outContent.toString();
        assertTrue(output.contains("Loan Records"));

        // Test sorting by author
        outContent.reset();
        libraryService.viewLoansSorted(LoanSortCriteria.BOOK_AUTHOR, false, true);
        output = outContent.toString();
        assertTrue(output.contains("Loan Records"));

        // Test sorting by due date
        outContent.reset();
        libraryService.viewLoansSorted(LoanSortCriteria.DUE_DATE, true, false);
        output = outContent.toString();
        assertTrue(output.contains("Loan Records"));

        // Test sorting by return date
        outContent.reset();
        libraryService.viewLoansSorted(LoanSortCriteria.RETURN_DATE, false, true);
        output = outContent.toString();
        assertTrue(output.contains("Loan Records"));

        // Test sorting by loan date
        outContent.reset();
        libraryService.viewLoansSorted(LoanSortCriteria.LOAN_DATE, true, false);
        output = outContent.toString();
        assertTrue(output.contains("Loan Records"));

        // Test sorting by status
        outContent.reset();
        libraryService.viewLoansSorted(LoanSortCriteria.STATUS, false, true);
        output = outContent.toString();
        assertTrue(output.contains("Loan Records"));
    }

    @Test
    void testUpdatePreferences() {
        libraryService.updateBookSortPreferences(SortCriteria.AUTHOR, false);
        assertEquals(SortCriteria.AUTHOR, dummyPrefs.getDefaultBookSortCriteria());
        assertFalse(dummyPrefs.isDefaultSortAscending());

        libraryService.updateLoanSortPreferences(LoanSortCriteria.BOOK_TITLE, false);
        assertEquals(LoanSortCriteria.BOOK_TITLE, dummyPrefs.getDefaultLoanSortCriteria());
        assertFalse(dummyPrefs.isDefaultSortAscending());

        libraryService.updateSearchPreferences(SearchCriteria.ISBN);
        assertEquals(SearchCriteria.ISBN, dummyPrefs.getDefaultSearchCriteria());
    }

    @Test
    void testViewAllBooksSortedWithPreferences() {
        libraryService.addBook("123", "Alpha", "Author A");
        libraryService.addBook("456", "Beta", "Author B");
        dummyPrefs.setDefaultBookSortCriteria(SortCriteria.TITLE);
        dummyPrefs.setDefaultSortAscending(true);
        libraryService.viewAllBooksSortedWithPreferences();
        String output = outContent.toString();
        assertTrue(output.contains("Library Books (Sorted by Title"));
    }

    @Test
    void testLogout() {
        libraryService.setCurrentUser(new User("1", false));
        libraryService.logout();
        assertNull(libraryService.getCurrentUser());
    }
    
    @Test
    void testSaveData() {
        libraryService.saveData();
        generalStorageMock.verify(() -> GeneralStorage.saveBookList(Mockito.anyString(), Mockito.eq(dummyBookList)));
    }
    
    @Test
    void testViewAllBooks() {
        libraryService.addBook("123", "Test Book", "Author A");
        libraryService.addBook("456", "Another Book", "Author B");
        outContent.reset();
        
        libraryService.viewAllBooks();
        
        String output = outContent.toString();
        assertTrue(output.contains("Books in Library:"));
        assertTrue(output.contains("Test Book"));
        assertTrue(output.contains("Another Book"));
    }
    
    @Test
    void testGetMyLoansWithReturnedFilter() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Book 1", "Author 1");
        libraryService.addBook("456", "Book 2", "Author 2");
        
        // Create and return a book
        libraryService.loanBook("123");
        libraryService.returnBook("123");
        
        // Create another loan
        libraryService.loanBook("456");
        
        // Test with includeReturned=true
        List<Loan> loansWithReturned = libraryService.getMyLoans(true);
        assertEquals(2, loansWithReturned.size());
        
        // Test with includeReturned=false
        List<Loan> loansWithoutReturned = libraryService.getMyLoans(false);
        assertEquals(1, loansWithoutReturned.size());
        assertEquals("456", loansWithoutReturned.get(0).getBook().getIsbn());
    }
    
    @Test
    void testGetMyLoansNoUser() {
        Exception exception = assertThrows(IllegalStateException.class, () ->
                libraryService.getMyLoans()
        );
        assertTrue(exception.getMessage().contains("No user is currently logged in"));
    }
    
    @Test
    void testGetAllLoanRecords() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Book 1", "Author 1");
        libraryService.addBook("456", "Book 2", "Author 2");
        
        libraryService.loanBook("123");
        libraryService.loanBook("456");
        libraryService.returnBook("123");
        
        List<Loan> allLoans = libraryService.getAllLoanRecords();
        assertEquals(2, allLoans.size());
    }
    
    @Test
    void testViewLoansSortedOverloads() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Alpha", "Author A");
        libraryService.addBook("456", "Beta", "Author B");
        libraryService.loanBook("123");
        libraryService.loanBook("456");
        
        outContent.reset();
        libraryService.viewLoansSorted(LoanSortCriteria.BOOK_TITLE);
        assertTrue(outContent.toString().contains("Loan Records (Sorted by Book Title, Ascending)"));
        
        outContent.reset();
        libraryService.viewLoansSorted(LoanSortCriteria.BOOK_AUTHOR, false);
        assertTrue(outContent.toString().contains("Loan Records (Sorted by Book Author, Descending)"));
    }
    
    @Test
    void testViewCurrentLoansSorted() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Alpha", "Author A");
        libraryService.addBook("456", "Beta", "Author B");
        libraryService.loanBook("123");
        libraryService.loanBook("456");
        libraryService.returnBook("123");
        
        outContent.reset();
        libraryService.viewCurrentLoansSorted(LoanSortCriteria.BOOK_TITLE);
        String output = outContent.toString();
        assertTrue(output.contains("Loan Records"));
        assertFalse(output.contains("Alpha")); // Returned book shouldn't appear
        assertTrue(output.contains("Beta")); // Current loan should appear
        
        outContent.reset();
        libraryService.viewCurrentLoansSorted(LoanSortCriteria.BOOK_TITLE, false);
        assertTrue(outContent.toString().contains("Loan Records (Sorted by Book Title, Descending)"));
    }
    
    @Test
    void testViewAllBooksSorted() {
        libraryService.addBook("123", "Alpha", "Author A");
        libraryService.addBook("456", "Beta", "Author B");
        
        outContent.reset();
        libraryService.viewAllBooksSorted(SortCriteria.TITLE);
        String output = outContent.toString();
        assertTrue(output.contains("Library Books (Sorted by Title, Ascending)"));
        
        outContent.reset();
        libraryService.viewAllBooksSorted(SortCriteria.AUTHOR, false);
        output = outContent.toString();
        assertTrue(output.contains("Library Books (Sorted by Author, Descending)"));
    }
    
    @Test
    void testSearchAndSortBooks() {
        libraryService.addBook("123", "Java Programming", "Author A");
        libraryService.addBook("456", "Python Basics", "Author B");
        libraryService.addBook("789", "Advanced Java", "Author C");
        
        List<Book> results = libraryService.searchAndSortBooks("Java", SearchCriteria.TITLE, SortCriteria.AUTHOR, true);
        assertEquals(2, results.size());
        assertEquals("Java Programming", results.get(0).getTitle()); // Author A comes before Author C alphabetically
        assertEquals("Advanced Java", results.get(1).getTitle());
    }
    
    @Test
    void testGetAllBooks() {
        libraryService.addBook("123", "Book 1", "Author 1");
        libraryService.addBook("456", "Book 2", "Author 2");
        
        List<Book> allBooks = libraryService.getAllBooks();
        assertEquals(2, allBooks.size());
    }
    
    @Test
    void testSortBooks() {
        libraryService.addBook("123", "Zebra", "Author Z");
        libraryService.addBook("456", "Apple", "Author A");
        
        List<Book> sortedByTitle = libraryService.sortBooks(SortCriteria.TITLE, true);
        assertEquals(2, sortedByTitle.size());
        assertEquals("Apple", sortedByTitle.get(0).getTitle());
        assertEquals("Zebra", sortedByTitle.get(1).getTitle());
        
        List<Book> sortedByTitleDesc = libraryService.sortBooks(SortCriteria.TITLE, false);
        assertEquals("Zebra", sortedByTitleDesc.get(0).getTitle());
        assertEquals("Apple", sortedByTitleDesc.get(1).getTitle());
    }
    
    @Test
    void testSortLoans() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Alpha", "Author A");
        libraryService.addBook("456", "Beta", "Author B");
        libraryService.loanBook("123");
        libraryService.loanBook("456");
        
        List<Loan> loans = libraryService.getAllLoanRecords();
        List<Loan> sortedByTitle = libraryService.sortLoans(loans, LoanSortCriteria.BOOK_TITLE, true);
        assertEquals(2, sortedByTitle.size());
        assertEquals("Alpha", sortedByTitle.get(0).getBook().getTitle());
        assertEquals("Beta", sortedByTitle.get(1).getBook().getTitle());
        
        List<Loan> sortedByTitleDesc = libraryService.sortLoans(loans, LoanSortCriteria.BOOK_TITLE, false);
        assertEquals("Beta", sortedByTitleDesc.get(0).getBook().getTitle());
        assertEquals("Alpha", sortedByTitleDesc.get(1).getBook().getTitle());

        List<Loan> sortedByAuthor = libraryService.sortLoans(loans, LoanSortCriteria.BOOK_AUTHOR, true);
        assertEquals(2, sortedByAuthor.size());
        assertEquals("Author A", sortedByAuthor.get(0).getBook().getAuthor());
        assertEquals("Author B", sortedByAuthor.get(1).getBook().getAuthor());

        List<Loan> sortedByAuthorDesc = libraryService.sortLoans(loans, LoanSortCriteria.BOOK_AUTHOR, false);
        assertEquals("Author B", sortedByAuthorDesc.get(0).getBook().getAuthor());
        assertEquals("Author A", sortedByAuthorDesc.get(1).getBook().getAuthor());
    }
    
    @Test
    void testSortLoansByOtherCriteria() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Alpha", "Jones");
        libraryService.addBook("456", "Beta", "Adams");
        libraryService.loanBook("123");
        libraryService.loanBook("456");
        
        // One book is returned, one is still on loan
        libraryService.returnBook("123");
        
        List<Loan> loans = libraryService.getAllLoanRecords();
        
        // Test sorting by author
        List<Loan> sortedByAuthor = libraryService.sortLoans(loans, LoanSortCriteria.BOOK_AUTHOR, true);
        assertEquals("Adams", sortedByAuthor.get(0).getBook().getAuthor());
        assertEquals("Jones", sortedByAuthor.get(1).getBook().getAuthor());
        
        // Test sorting by ISBN
        List<Loan> sortedByIsbn = libraryService.sortLoans(loans, LoanSortCriteria.BOOK_ISBN, true);
        assertEquals("123", sortedByIsbn.get(0).getBook().getIsbn());
        assertEquals("456", sortedByIsbn.get(1).getBook().getIsbn());
        
        // Test sorting by status (returned comes before on loan)
        List<Loan> sortedByStatus = libraryService.sortLoans(loans, LoanSortCriteria.STATUS, true);
        assertTrue(sortedByStatus.get(0).isReturned());
        assertFalse(sortedByStatus.get(1).isReturned());
        
        // Test sorting by loan date
        List<Loan> sortedByLoanDate = libraryService.sortLoans(loans, LoanSortCriteria.LOAN_DATE, true);
        assertEquals(2, sortedByLoanDate.size());
        
        // Test sorting by due date
        List<Loan> sortedByDueDate = libraryService.sortLoans(loans, LoanSortCriteria.DUE_DATE, true);
        assertEquals(2, sortedByDueDate.size());
        
        // Test sorting by return date (null values handled specially)
        List<Loan> sortedByReturnDate = libraryService.sortLoans(loans, LoanSortCriteria.RETURN_DATE, true);
        assertTrue(sortedByReturnDate.get(0).isReturned()); // Returned loan comes first
        assertFalse(sortedByReturnDate.get(1).isReturned()); // Non-returned loan comes last
    }
    
    @Test
    void testGetUserPreferences() {
        UserPreferences prefs = libraryService.getUserPreferences();
        assertSame(dummyPrefs, prefs);
    }
    
    @Test
    void testSaveUserPreferences() {
        libraryService.saveUserPreferences();
        generalStorageMock.verify(() -> GeneralStorage.saveUserPreferences(Mockito.anyString(), Mockito.eq(dummyPrefs)));
    }
    
    @Test
    void testViewLoansSortedWithPreferences() {
        User user = new User("1", false);
        libraryService.setCurrentUser(user);
        libraryService.addBook("123", "Alpha", "Author A");
        libraryService.loanBook("123");
        
        // Set preferences
        dummyPrefs.setDefaultLoanSortCriteria(LoanSortCriteria.BOOK_TITLE);
        dummyPrefs.setDefaultSortAscending(true);
        dummyPrefs.setShowReturnedLoans(false);
        
        outContent.reset();
        libraryService.viewLoansSortedWithPreferences();
        
        String output = outContent.toString();
        assertTrue(output.contains("Loan Records"));
    }
    
    @Test
    void testReturnBookNonExistent() {
        libraryService.returnBook("nonexistent");
        assertTrue(outContent.toString().contains("Book not found"));
    }
}
