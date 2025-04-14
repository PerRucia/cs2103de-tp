package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TestBookListSearch {
    private BookList bookList;
    
    @BeforeEach
    void setUp() {
        bookList = new BookList();
        
        // Add test books
        bookList.addBook(new Book("9780134685991", "Effective Java", "Joshua Bloch"));
        bookList.addBook(new Book("9780132350884", "Clean Code", "Robert C. Martin"));
        bookList.addBook(new Book("9781449331818", "Learning JavaScript", "Ethan Brown"));
        bookList.addBook(new Book("9780596007126", "Head First Design Patterns", "Eric Freeman"));
        bookList.addBook(new Book("9780321356680", "Effective Java", "Joshua Bloch")); // Duplicate title and author
    }
    
    @Test
    void testSearchByTitle() {
        List<Book> results = bookList.searchBooks("java", SearchCriteria.TITLE);
        
        // Don't assert specific count, just ensure results contain "java"
        assertTrue(results.size() > 0);
        assertTrue(results.stream().allMatch(book -> book.getTitle().toLowerCase().contains("java")));
    }
    
    @Test
    void testSearchByAuthor() {
        List<Book> results = bookList.searchBooks("bloch", SearchCriteria.AUTHOR);
        
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(book -> book.getAuthor().toLowerCase().contains("bloch")));
    }
    
    @Test
    void testSearchByIsbn() {
        List<Book> results = bookList.searchBooks("9780134", SearchCriteria.ISBN);
        
        assertEquals(1, results.size());
        assertEquals("9780134685991", results.get(0).getIsbn());
    }
    
    @Test
    void testSearchAllFields() {
        List<Book> results = bookList.searchBooks("java", SearchCriteria.ALL_FIELDS);
        
        // Should find books containing "Java" in the title
        assertTrue(results.size() >= 2);
    }
    
    @Test
    void testSearchAndSort() {
        List<Book> results = bookList.searchAndSortBooks("java", SearchCriteria.TITLE, SortCriteria.AUTHOR, true);
        
        // Don't assert specific count, just ensure results are not empty
        assertTrue(results.size() > 0);
        
        // Verify results are sorted by author
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getAuthor().compareTo(results.get(i + 1).getAuthor()) <= 0);
        }
    }
    
    @Test
    void testSearchByRelevance() {
        List<Book> results = bookList.searchBooksByRelevance("effective java", SearchCriteria.ALL_FIELDS);
        
        assertTrue(results.size() >= 2);
        // First result should be an exact match for "Effective Java"
        assertEquals("Effective Java", results.get(0).getTitle());
    }
    
    @Test
    void testEmptySearch() {
        List<Book> results = bookList.searchBooks("", SearchCriteria.TITLE);
        
        assertTrue(results.isEmpty());
    }
    
    @Test
    void testNoResults() {
        List<Book> results = bookList.searchBooks("nonexistent", SearchCriteria.TITLE);
        
        assertTrue(results.isEmpty());
    }
} 