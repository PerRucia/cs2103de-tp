package storage;

import models.Book;
import models.BookList;
import models.BookStatus;
import models.UserPreferences;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeneralStorageTest {

    @TempDir
    Path tempDir;

    private File bookFile;
    private File prefsFile;

    @BeforeEach
    public void setUp() throws Exception {
        // Create temporary files for book list and user prefs
        bookFile = tempDir.resolve("bookTest.txt").toFile();
        prefsFile = tempDir.resolve("prefsTest.dat").toFile();
    }

    @Test
    public void testLoadBookListNonExistentFile() {
        // Provide a file that does not exist
        BookList list = GeneralStorage.loadBookList("nonexistentFile.txt");
        // Expect an empty book list
        assertNotNull(list);
        assertTrue(list.getBooks().isEmpty());
    }

    @Test
    public void testSaveAndLoadBookList() throws Exception {
        // Create a book list with one real book
        BookList bookList = new BookList();
        Book book = new Book("1234567890", "Test Book", "Test Author");
        bookList.addBook(book);
        // Save using our temporary file
        GeneralStorage.saveBookList(bookFile.getAbsolutePath(), bookList);
        // Validate by reading file content
        try (BufferedReader reader = new BufferedReader(new FileReader(bookFile))) {
            String line = reader.readLine();
            assertNotNull(line);
            // The saved line should contain our book details
            assertTrue(line.contains("1234567890"));
            assertTrue(line.contains("Test Book"));
            assertTrue(line.contains("Test Author"));
            assertTrue(line.contains(BookStatus.AVAILABLE.toString()));
        }
        // Load back the file and verify
        BookList loaded = GeneralStorage.loadBookList(bookFile.getAbsolutePath());
        Map<String, Book> books = loaded.getBooks();
        assertEquals(1, books.size());
        Book loadedBook = books.get("1234567890");
        assertNotNull(loadedBook);
        assertEquals("Test Book", loadedBook.getTitle());
    }

    @Test
    public void testSaveAndLoadUserPreferences() {
        // Create a non-default UserPreferences
        UserPreferences prefs = new UserPreferences();
        prefs.setDefaultSortAscending(false);
        prefs.setDefaultBookSortCriteria(models.SortCriteria.AUTHOR);
        prefs.setDefaultLoanSortCriteria(models.LoanSortCriteria.DUE_DATE);
        prefs.setDefaultSearchCriteria(models.SearchCriteria.ISBN);
        // Save preferences
        GeneralStorage.saveUserPreferences(prefsFile.getAbsolutePath(), prefs);
        // Load the preferences back
        UserPreferences loadedPrefs = GeneralStorage.loadUserPreferences(prefsFile.getAbsolutePath());
        // Check that the loaded fields match the saved ones
        assertFalse(loadedPrefs.isDefaultSortAscending());
        assertEquals(models.SortCriteria.AUTHOR, loadedPrefs.getDefaultBookSortCriteria());
        assertEquals(models.LoanSortCriteria.DUE_DATE, loadedPrefs.getDefaultLoanSortCriteria());
        assertEquals(models.SearchCriteria.ISBN, loadedPrefs.getDefaultSearchCriteria());
    }

    @Test
    public void testLoadUserPreferencesNonExistentFile() {
        // Pass in a file that does not exist; should return default prefs
        UserPreferences prefs = GeneralStorage.loadUserPreferences("nonexistentPrefs.dat");
        assertNotNull(prefs);
        // Default values as defined in the constructor
        assertTrue(prefs.isDefaultSortAscending());
        assertEquals(models.SortCriteria.TITLE, prefs.getDefaultBookSortCriteria());
    }

    @Test
    public void testSaveBookListWithMockedBook() throws Exception {
        // Use Mockito to create a dummy Book
        BookList bookList = new BookList();
        Book mockBook = Mockito.mock(Book.class);
        when(mockBook.getIsbn()).thenReturn("MOCKISBN");
        when(mockBook.getTitle()).thenReturn("Mock Title");
        when(mockBook.getAuthor()).thenReturn("Mock Author");
        when(mockBook.getStatus()).thenReturn(BookStatus.CHECKED_OUT);

        // Here, add the mocked Book to bookList via direct map insertion if needed
        // Otherwise, override addBook to allow the mock to be added.
        // Using reflection is an option but here we simulate addBook:
        bookList.addBook(mockBook);

        GeneralStorage.saveBookList(bookFile.getAbsolutePath(), bookList);
        // Read the file and check for mock data
        try (BufferedReader reader = new BufferedReader(new FileReader(bookFile))) {
            String line = reader.readLine();
            assertNotNull(line);
            assertTrue(line.contains("MOCKISBN"));
            assertTrue(line.contains("Mock Title"));
            assertTrue(line.contains("Mock Author"));
            assertTrue(line.contains(BookStatus.CHECKED_OUT.toString()));
        }
    }
}
