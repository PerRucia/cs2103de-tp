package storage;

import models.Book;
import models.BookList;
import models.BookStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public class TestGeneralStorage {

    @TempDir
    Path tempDir;

    private Path tempFile;

    @BeforeEach
    void setUp() throws Exception {
        tempFile = tempDir.resolve("testBookDatabase.txt");
        // Create the file to be used in tests where needed.
        File file = tempFile.toFile();
        if (!file.exists()) {
            file.createNewFile();
        }
    }

    @AfterEach
    void tearDown() {
        File file = tempFile.toFile();
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Test the saveBookList and loadBookList methods of the GeneralStorage class.
     * This test checks if the book list is saved and loaded correctly.
     */
    @Test
    void testSaveAndLoadBookList() {
        // Create a BookList with two books.
        BookList bookList = new BookList();
        Book book1 = new Book("1111111111", "Book One", "Author A");
        Book book2 = new Book("2222222222", "Book Two", "Author B");
        bookList.addBook(book1);
        bookList.addBook(book2);

        // Save the BookList to the temporary file.
        GeneralStorage.saveBookList(tempFile.toString(), bookList);

        // Load the BookList from the temporary file.
        BookList loadedList = GeneralStorage.loadBookList(tempFile.toString());
        Assertions.assertNotNull(loadedList);
        Map<String, Book> books = loadedList.getAllBooks();
        Assertions.assertEquals(2, books.size());

        // Verify each book's data and default status.
        Book loadedBook1 = books.get("1111111111");
        Assertions.assertNotNull(loadedBook1);
        Assertions.assertEquals("Book One", loadedBook1.getTitle());
        Assertions.assertEquals(BookStatus.AVAILABLE, loadedBook1.getStatus());
    }

    /**
     * Test the loadBookList method of the GeneralStorage class when the file does not exist.
     * This test checks if the method returns null when attempting to load from a non-existent file.
     */
    @Test
    void testLoadBookListFileNotFound() {
        // Delete the file so that it does not exist.
        File file = tempFile.toFile();
        file.delete();

        // Expect loadBookList() to return null when the file is missing.
        BookList loadedList = GeneralStorage.loadBookList(tempFile.toString());
        Assertions.assertNull(loadedList);

        // Check that saveBookList() does not throw an exception when the file is missing.
        Assertions.assertDoesNotThrow(() -> GeneralStorage.saveBookList(
                "/random/path/to/file.txt", new BookList()));
    }
}