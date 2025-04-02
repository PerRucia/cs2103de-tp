package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class TestBookListSorting {
    private BookList bookList;
    
    @BeforeEach
    void setUp() {
        bookList = new BookList();
        
        // 添加测试图书，故意打乱顺序
        bookList.addBook(new Book("9780134685991", "Effective Java", "Joshua Bloch"));
        bookList.addBook(new Book("9780132350884", "Clean Code", "Robert C. Martin"));
        bookList.addBook(new Book("9781449331818", "Learning JavaScript", "Ethan Brown"));
        
        // 设置不同状态
        Book book1 = bookList.getBook("9780134685991");
        book1.setStatus(BookStatus.CHECKED_OUT);
        
        Book book2 = bookList.getBook("9780132350884");
        book2.setStatus(BookStatus.AVAILABLE);
        
        Book book3 = bookList.getBook("9781449331818");
        book3.setStatus(BookStatus.OVERDUE);
    }
    
    @Test
    void testSortByTitle() {
        List<Book> sorted = bookList.getSortedBooks(SortCriteria.TITLE);
        
        assertEquals(3, sorted.size());
        assertEquals("Clean Code", sorted.get(0).getTitle());
        assertEquals("Effective Java", sorted.get(1).getTitle());
        assertEquals("Learning JavaScript", sorted.get(2).getTitle());
    }
    
    @Test
    void testSortByAuthor() {
        List<Book> sorted = bookList.getSortedBooks(SortCriteria.AUTHOR);
        
        assertEquals(3, sorted.size());
        assertEquals("Ethan Brown", sorted.get(0).getAuthor());
        assertEquals("Joshua Bloch", sorted.get(1).getAuthor());
        assertEquals("Robert C. Martin", sorted.get(2).getAuthor());
    }
    
    @Test
    void testSortByIsbn() {
        List<Book> sorted = bookList.getSortedBooks(SortCriteria.ISBN);
        
        assertEquals(3, sorted.size());
        assertEquals("9780132350884", sorted.get(0).getIsbn());
        assertEquals("9780134685991", sorted.get(1).getIsbn());
        assertEquals("9781449331818", sorted.get(2).getIsbn());
    }
    
    @Test
    void testSortByStatus() {
        List<Book> sorted = bookList.getSortedBooks(SortCriteria.STATUS);
        
        assertEquals(3, sorted.size());
        assertEquals(BookStatus.AVAILABLE, sorted.get(0).getStatus());
        assertEquals(BookStatus.CHECKED_OUT, sorted.get(1).getStatus());
        assertEquals(BookStatus.OVERDUE, sorted.get(2).getStatus());
    }
    
    @Test
    void testSortDescending() {
        List<Book> sorted = bookList.getSortedBooks(SortCriteria.TITLE, false);
        
        assertEquals(3, sorted.size());
        assertEquals("Learning JavaScript", sorted.get(0).getTitle());
        assertEquals("Effective Java", sorted.get(1).getTitle());
        assertEquals("Clean Code", sorted.get(2).getTitle());
    }
} 