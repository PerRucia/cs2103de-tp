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

    /**
     * 根据搜索条件和查询字符串搜索图书
     * @param query 查询字符串
     * @param criteria 搜索条件
     * @return 匹配的图书列表
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
     * 检查图书是否匹配搜索条件
     * @param book 要检查的图书
     * @param query 规范化后的查询字符串（小写且去除首尾空格）
     * @param criteria 搜索条件
     * @return 如果图书匹配搜索条件则返回true
     */
    private boolean matchesSearchCriteria(Book book, String query, SearchCriteria criteria) {
        switch (criteria) {
            case TITLE:
                return book.getTitle().toLowerCase().contains(query);
            case AUTHOR:
                return book.getAuthor().toLowerCase().contains(query);
            case ISBN:
                return book.getIsbn().toLowerCase().contains(query);
            case ALL_FIELDS:
                return book.getTitle().toLowerCase().contains(query) ||
                       book.getAuthor().toLowerCase().contains(query) ||
                       book.getIsbn().toLowerCase().contains(query);
            default:
                return false;
        }
    }

    /**
     * 搜索图书并对结果进行排序
     * @param query 查询字符串
     * @param searchCriteria 搜索条件
     * @param sortCriteria 排序条件
     * @param ascending 是否升序排列
     * @return 排序后的搜索结果
     */
    public List<Book> searchAndSortBooks(String query, SearchCriteria searchCriteria, 
                                        SortCriteria sortCriteria, boolean ascending) {
        List<Book> searchResults = searchBooks(query, searchCriteria);
        
        if (searchResults.isEmpty()) {
            return searchResults;
        }
        
        Comparator<Book> comparator;
        
        switch (sortCriteria) {
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
        
        searchResults.sort(comparator);
        return searchResults;
    }

    /**
     * 搜索图书并对结果进行排序（默认升序）
     * @param query 查询字符串
     * @param searchCriteria 搜索条件
     * @param sortCriteria 排序条件
     * @return 排序后的搜索结果
     */
    public List<Book> searchAndSortBooks(String query, SearchCriteria searchCriteria, 
                                        SortCriteria sortCriteria) {
        return searchAndSortBooks(query, searchCriteria, sortCriteria, true);
    }

    /**
     * 搜索图书（默认按相关性排序）
     * @param query 查询字符串
     * @param criteria 搜索条件
     * @return 按相关性排序的搜索结果
     */
    public List<Book> searchBooksByRelevance(String query, SearchCriteria criteria) {
        if (query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        String normalizedQuery = query.toLowerCase().trim();
        
        // 创建一个带有相关性分数的图书列表
        List<Map.Entry<Book, Integer>> scoredBooks = books.values().stream()
                .map(book -> new AbstractMap.SimpleEntry<>(book, calculateRelevanceScore(book, normalizedQuery, criteria)))
                .filter(entry -> entry.getValue() > 0) // 只保留匹配的图书
                .collect(Collectors.toList());
        
        // 按相关性分数降序排序
        scoredBooks.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
        
        // 提取排序后的图书
        return scoredBooks.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * 计算图书与查询的相关性分数
     * @param book 要计算的图书
     * @param query 规范化后的查询字符串
     * @param criteria 搜索条件
     * @return 相关性分数（越高越相关）
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
            case ALL_FIELDS:
                score += calculateFieldScore(book.getTitle(), query) * 3; // 标题匹配权重更高
                score += calculateFieldScore(book.getAuthor(), query) * 2; // 作者匹配权重次之
                score += calculateFieldScore(book.getIsbn(), query);
                break;
        }
        
        return score;
    }

    /**
     * 计算字段与查询的匹配分数
     * @param field 字段值
     * @param query 查询字符串
     * @return 匹配分数
     */
    private int calculateFieldScore(String field, String query) {
        String normalizedField = field.toLowerCase();
        
        // 完全匹配得高分
        if (normalizedField.equals(query)) {
            return 10;
        }
        
        // 开头匹配得中等分数
        if (normalizedField.startsWith(query)) {
            return 5;
        }
        
        // 包含匹配得低分
        if (normalizedField.contains(query)) {
            return 3;
        }
        
        // 不匹配得零分
        return 0;
    }

    /**
     * Clears all books from the list.
     */
    public void clear() {
        books.clear();
    }
}