package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookList {
    private Map<String, Book> books;

    public BookList() {
        this.books = new HashMap<>();
    }

    /**
     * Gets a book from the list by its ISBN.
     * @param isbn The ISBN of the book to retrieve.
     * @return The book with the specified ISBN, or null if not found.
     */
    public Book getBook(String isbn) {
        return books.get(isbn);
    }

    /**
     * Adds a book to the list if it is not already present.
     * @param book The book to be added.
     * @throws IllegalArgumentException if the book or ISBN is null or empty.
     */
    public void addBook(Book book) throws IllegalArgumentException {
        if (book != null && book.getIsbn() != null && !book.getIsbn().isEmpty()) {
            books.put(book.getIsbn(), book);
            book.setStatus(BookStatus.AVAILABLE);
        } else {
            throw new IllegalArgumentException("Book or ISBN cannot be null or empty.");
        }
    }

    /**
     * Removes a book from the list if it is available.
     * @param book The book to be removed.
     * @throws IllegalStateException if the book is not available.
     */
    public void removeBook(Book book) throws IllegalStateException {
        if (book != null && book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.OUT_OF_CIRCULATION);
            books.remove(book.getIsbn());
        } else {
            throw new IllegalStateException("Cannot remove book that is not available.");
        }
    }

    /**
     * Loans a book to a user if it is available.
     * @param book The book to be loaned.
     * @throws IllegalStateException if the book is not available.
     */
    public void loanBook(Book book) throws IllegalStateException {
        if (book != null && book.getStatus() == BookStatus.AVAILABLE) {
            book.setStatus(BookStatus.CHECKED_OUT);
        } else {
            throw new IllegalStateException("Cannot loan a book that is not available.");
        }
    }

    /**
     * Returns a book to the list if it is checked out or overdue.
     * @param book The book to be returned.
     * @throws IllegalStateException if the book is not checked out or overdue.
     */
    public void returnBook(Book book) throws IllegalStateException {
        if (book != null && (book.getStatus() == BookStatus.CHECKED_OUT ||
                book.getStatus() == BookStatus.OVERDUE)) {
            book.setStatus(BookStatus.AVAILABLE);
        } else {
            throw new IllegalStateException("Cannot return a book that is not checked out.");
        }
    }

    public void overdueBook(Book book) {
        if (book != null && book.getStatus() == BookStatus.CHECKED_OUT) {
            book.setStatus(BookStatus.OVERDUE);
        } else {
            throw new IllegalStateException("Cannot mark a book as overdue " +
                    "that is not checked out.");
        }
    }

    /**
     * Checks if a book is in the list.
     * @param isbn The ISBN of the book to check.
     * @return true if the book is in the list, false otherwise.
     */
    public boolean containsBook(String isbn) {
        return books.containsKey(isbn);
    }

    /**
     * Gets all books in the list.
     * @return A map of all books with their ISBN as the key.
     */
    public Map<String, Book> getAllBooks() {
        return new HashMap<>(books);
    }

    /**
     * 获取按指定条件排序的图书列表
     * @param criteria 排序条件
     * @param ascending 是否升序排列
     * @return 排序后的图书列表
     */
    public List<Book> getSortedBooks(SortCriteria criteria, boolean ascending) {
        List<Book> bookList = new ArrayList<>(books.values());
        
        Comparator<Book> comparator;
        
        switch (criteria) {
            case TITLE:
                comparator = Comparator.comparing(Book::getTitle);
                break;
            case AUTHOR:
                comparator = Comparator.comparing(Book::getAuthor);
                break;
            case ISBN:
                comparator = Comparator.comparing(Book::getIsbn);
                break;
            case STATUS:
                comparator = Comparator.comparing(book -> book.getStatus().toString());
                break;
            default:
                comparator = Comparator.comparing(Book::getTitle);
        }
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        bookList.sort(comparator);
        return bookList;
    }

    /**
     * 获取按指定条件升序排序的图书列表
     * @param criteria 排序条件
     * @return 排序后的图书列表
     */
    public List<Book> getSortedBooks(SortCriteria criteria) {
        return getSortedBooks(criteria, true);
    }
}