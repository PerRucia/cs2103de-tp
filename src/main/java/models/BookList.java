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
     * Gets a list of books sorted by the specified criteria
     * @param criteria Sorting criteria
     * @param ascending Whether to sort in ascending order
     * @return Sorted list of books
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
     * Gets a list of books sorted by the specified criteria in ascending order
     * @param criteria Sorting criteria
     * @return Sorted list of books
     */
    public List<Book> getSortedBooks(SortCriteria criteria) {
        return getSortedBooks(criteria, true);
    }

    /**
     * Searches for books based on search criteria and query string
     * @param query Search query string
     * @param criteria Search criteria
     * @return List of matching books
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
     * Checks if a book matches the search criteria
     * @param book Book to check
     * @param query Normalized query string (lowercase and trimmed)
     * @param criteria Search criteria
     * @return true if the book matches the search criteria
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
     * Searches for books and sorts the results
     * @param query Search query string
     * @param searchCriteria Search criteria
     * @param sortCriteria Sorting criteria
     * @param ascending Whether to sort in ascending order
     * @return Sorted search results
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
     * Searches for books and sorts the results in ascending order
     * @param query Search query string
     * @param searchCriteria Search criteria
     * @param sortCriteria Sorting criteria
     * @return Sorted search results
     */
    public List<Book> searchAndSortBooks(String query, SearchCriteria searchCriteria, 
                                        SortCriteria sortCriteria) {
        return searchAndSortBooks(query, searchCriteria, sortCriteria, true);
    }

    /**
     * Searches for books (sorted by relevance by default)
     * @param query Search query string
     * @param criteria Search criteria
     * @return Search results sorted by relevance
     */
    public List<Book> searchBooksByRelevance(String query, SearchCriteria criteria) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedQuery = query.toLowerCase().trim();
        
        // Create a list of books with relevance scores
        List<Map.Entry<Book, Integer>> scoredBooks = books.values().stream()
                .map(book -> new AbstractMap.SimpleEntry<>(book, calculateRelevanceScore(book, normalizedQuery, criteria)))
                .filter(entry -> entry.getValue() > 0) // Only keep matching books
                .collect(Collectors.toList());
        
        // Sort by relevance score in descending order
        scoredBooks.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // Extract sorted books
        return scoredBooks.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Calculate relevance score between book and query
     * @param book Book to calculate score for
     * @param query Normalized query string
     * @param criteria Search criteria
     * @return Relevance score (higher means more relevant)
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
                score += calculateFieldScore(book.getTitle(), query) * 3; // Higher weight for title matches
                score += calculateFieldScore(book.getAuthor(), query) * 2; // Medium weight for author matches
                score += calculateFieldScore(book.getIsbn(), query);
                break;
            default:
                break;
        }
        
        return score;
    }

    /**
     * Calculate match score between field and query
     * @param field Field value
     * @param query Query string
     * @return Match score
     */
    private int calculateFieldScore(String field, String query) {
        String normalizedField = field.toLowerCase();
        
        // Exact match gets highest score
        if (normalizedField.equals(query)) {
            return 10;
        }
        
        // Beginning match gets medium score
        if (normalizedField.startsWith(query)) {
            return 5;
        }
        
        // Contains match gets low score
        if (normalizedField.contains(query)) {
            return 3;
        }
        
        // No match gets zero score
        return 0;
    }

    /**
     * Clears all books from the list.
     */
    public void clear() {
        books.clear();
    }
}