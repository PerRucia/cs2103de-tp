package models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.AbstractMap;

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
    public Map<String, Book> getBooks() {
        return new HashMap<>(books);
    }

    /**
     * Get a list of books sorted by specified criteria
     * @param criteria Sorting criteria
     * @param ascending Whether to sort in ascending order
     * @return The sorted list of books
     */
    public List<Book> getSortedBooks(SortCriteria criteria, boolean ascending) {
        List<Book> bookList = new ArrayList<>(books.values());
        
        Comparator<Book> comparator = switch (criteria) {
            case TITLE -> Comparator.comparing(Book::getTitle);
            case AUTHOR -> Comparator.comparing(Book::getAuthor);
            case ISBN -> Comparator.comparing(Book::getIsbn);
            case STATUS -> Comparator.comparing(book -> book.getStatus().toString());
        };
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        bookList.sort(comparator);
        return bookList;
    }

    /**
     * Get a list of books sorted in ascending order according to the specified criteria
     * @param criteria sorting criteria
     * @return sorted book list
     */
    public List<Book> getSortedBooks(SortCriteria criteria) {
        return getSortedBooks(criteria, true);
    }

    /**
     * Search books based on search conditions and query string
     * @param query query string
     * @param criteria search conditions
     * @return matching book list
     */
    public List<Book> searchBooks(String query, SearchCriteria criteria) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedQuery = query.toLowerCase().trim();
        
        return books.values().stream()
                .filter(book -> matchesSearchCriteria(book, normalizedQuery, criteria))
                .collect(Collectors.toList());
    }

    /**
     * Check if the book matches the search criteria
     * @param book The book to be checked
     * @param query Normalized query string (lowercase and without leading and trailing spaces)
     * @param criteria Search criteria
     * @return Returns true if the book matches the search criteria
     */
    private boolean matchesSearchCriteria(Book book, String query, SearchCriteria criteria) {
        return switch (criteria) {
            case TITLE -> book.getTitle().toLowerCase().contains(query);
            case AUTHOR -> book.getAuthor().toLowerCase().contains(query);
            case ISBN -> book.getIsbn().toLowerCase().contains(query);
            case STATUS -> book.getStatus().toString().toLowerCase().contains(query);
            case ALL -> book.getTitle().toLowerCase().contains(query) ||
                       book.getAuthor().toLowerCase().contains(query) ||
                       book.getIsbn().toLowerCase().contains(query) ||
                       book.getStatus().toString().toLowerCase().contains(query);
        };
    }

    /**
     * Search books and sort the results
     * @param query query string
     * @param searchCriteria search criteria
     * @param sortCriteria sort criteria
     * @param ascending whether to sort in ascending order
     * @return sorted search results
     */
    public List<Book> searchAndSortBooks(String query, SearchCriteria searchCriteria, 
                                        SortCriteria sortCriteria, boolean ascending) {
        List<Book> searchResults = searchBooks(query, searchCriteria);
        
        if (searchResults.isEmpty()) {
            return searchResults;
        }
        
        Comparator<Book> comparator = switch (sortCriteria) {
            case TITLE -> Comparator.comparing(Book::getTitle);
            case AUTHOR -> Comparator.comparing(Book::getAuthor);
            case ISBN -> Comparator.comparing(Book::getIsbn);
            case STATUS -> Comparator.comparing(book -> book.getStatus().toString());
        };
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        searchResults.sort(comparator);
        return searchResults;
    }

    /**
     * Search books and sort the results (default ascending order)
     * @param query query string
     * @param searchCriteria search criteria
     * @param sortCriteria sort criteria
     * @return sorted search results
     */
    public List<Book> searchAndSortBooks(String query, SearchCriteria searchCriteria, 
                                        SortCriteria sortCriteria) {
        return searchAndSortBooks(query, searchCriteria, sortCriteria, true);
    }

    /**
     * Search books (sorted by relevance by default)
     * @param query query string
     * @param criteria search criteria
     * @return search results sorted by relevance
     */
    public List<Book> searchBooksByRelevance(String query, SearchCriteria criteria) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedQuery = query.toLowerCase().trim();
        
        // Create a list of books with relevance scores
        List<Map.Entry<Book, Integer>> scoredBooks = books.values().stream()
                .map(book -> new AbstractMap.SimpleEntry<>(book, calculateRelevanceScore(book, normalizedQuery, criteria)))
                .filter(entry -> entry.getValue() > 0) // 只保留匹配的图书
                .collect(Collectors.toList());
        
        // Sort by relevance score in descending order
        scoredBooks.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // Extract sorted books
        return scoredBooks.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate the relevance score of the book to the query
     * @param book The book to be calculated
     * @param query The normalized query string
     * @param criteria Search criteria
     * @return Relevance score (the higher the more relevant)
     */
    private int calculateRelevanceScore(Book book, String query, SearchCriteria criteria) {
        int score = 0;
        
        switch (criteria) {
            case TITLE:
                score += calculateFieldScore(book.getTitle(), query);
                break;
            case AUTHOR:
                score += calculateFieldScore(book.getAuthor(), query);
                break;
            case ISBN:
                score += calculateFieldScore(book.getIsbn(), query);
                break;
            case ALL:
                score += calculateFieldScore(book.getTitle(), query) * 3; // 标题匹配权重更高
                score += calculateFieldScore(book.getAuthor(), query) * 2; // 作者匹配权重次之
                score += calculateFieldScore(book.getIsbn(), query);
                break;
        }
        
        return score;
    }

    /**
     * Calculate the match score between the field and the query
     * @param field field value
     * @param query query string
     * @return match score
     */
    private int calculateFieldScore(String field, String query) {
        String normalizedField = field.toLowerCase();
        
        // Exact matches get high scores
        if (normalizedField.equals(query)) {
            return 10;
        }
        
        // The beginning match gets a medium score
        if (normalizedField.startsWith(query)) {
            return 5;
        }
        
        // Contains matching low score
        if (normalizedField.contains(query)) {
            return 3;
        }
        
        // No match will result in zero points
        return 0;
    }

    /**
     * Clears all books from the list.
     */
    public void clear() {
        books.clear();
    }
}