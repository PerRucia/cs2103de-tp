package service;

import models.*;
import storage.GeneralStorage;
import java.util.List;
import java.util.Comparator;
import java.time.LocalDate;
import java.util.ArrayList;

public class LibraryService {
    private static final String DATABASE_FILE = "src/main/resources/bookDatabase.txt";
    private final BookList bookList;
    private final LoanList loanList;
    private UserPreferences userPreferences;
    private static final String USER_PREFS_FILE = "user_preferences.dat";
    private User currentUser;

    public LibraryService() {
        BookList bookList1;
        bookList1 = GeneralStorage.loadBookList(DATABASE_FILE);
        if (bookList1 == null) {
            bookList1 = new BookList();
        }
        this.bookList = bookList1;
        this.loanList = new LoanList();
        
        // Load user preferences
        this.userPreferences = GeneralStorage.loadUserPreferences(USER_PREFS_FILE);
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void saveData() {
        GeneralStorage.saveBookList(DATABASE_FILE, bookList);
    }

    public void viewAllBooks() {
        System.out.println("\nBooks in Library:");
        bookList.getBooks().values().forEach(System.out::println);
    }

    public void addBook(String isbn, String title, String author) {
        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                throw new IllegalArgumentException("ISBN cannot be empty.");
            }
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty.");
            }
            if (author == null || author.trim().isEmpty()) {
                throw new IllegalArgumentException("Author cannot be empty.");
            }
            
            Book book = new Book(isbn, title, author);
            bookList.addBook(book);
            System.out.println("Book added successfully: " + book.getTitle());
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public void removeBook(String isbn) {
        try {
            if (isbn == null || isbn.trim().isEmpty()) {
                throw new IllegalArgumentException("ISBN cannot be empty.");
            }
            
            Book book = bookList.getBook(isbn);
            if (book == null) {
                throw new IllegalArgumentException("Book not found.");
            }
            
            if (book.getStatus() != BookStatus.AVAILABLE) {
                throw new IllegalStateException("Cannot remove book because it is currently " + 
                    book.getStatus().toString().toLowerCase() + ".");
            }
            
            bookList.removeBook(book);
            System.out.println("Book removed successfully.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e; // Re-throw to let UI handle the error
        }
    }

    public void loanBook(String isbn) {
        try {
            if (currentUser == null) {
                throw new IllegalStateException("No user is currently logged in.");
            }

            if (isbn == null || isbn.trim().isEmpty()) {
                throw new IllegalArgumentException("ISBN cannot be empty.");
            }
            
            Book book = bookList.getBook(isbn);
            if (book == null) {
                throw new IllegalArgumentException("Book not found with ISBN: " + isbn);
            }
            
            if (book.getStatus() != BookStatus.AVAILABLE) {
                throw new IllegalStateException("Book is not available for loan. Current status: " + book.getStatus());
            }
            
            // Create loan record using the current user
            loanList.createLoan(currentUser, book);
            
            // Update book status
            bookList.loanBook(book);
            
            System.out.println("Book loaned successfully: " + book.getTitle());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            throw e; // Re-throw to let UI handle the error
        }
    }

    public void returnBook(String isbn) {
        Book book = bookList.getBook(isbn);
        if (book != null) {
            try {
                bookList.returnBook(book);
                List<Loan> currentLoans = loanList.getCurrentLoans();
                for (Loan loan : currentLoans) {
                    if (loan.getBook().getIsbn().equals(isbn)) {
                        loan.returnBook();
                        break;
                    }
                }
                System.out.println("Book returned successfully.");
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    public List<Loan> viewLoans() {
        return loanList.getCurrentLoans();
    }

    public void viewLoansSorted(LoanSortCriteria criteria, boolean ascending, boolean currentOnly) {
        List<Loan> loans;
        if (currentOnly) {
            loans = loanList.getCurrentLoans();
        } else {
            loans = loanList.getAllLoans();
        }
        
        Comparator<Loan> comparator;
        switch (criteria) {
            case LOAN_DATE:
                comparator = Comparator.comparing(Loan::getLoanDate);
                break;
            case DUE_DATE:
                comparator = Comparator.comparing(Loan::getDueDate);
                break;
            case RETURN_DATE:
                comparator = (loan1, loan2) -> {
                    if (loan1.getReturnDate() == null && loan2.getReturnDate() == null) {
                        return 0;
                    } else if (loan1.getReturnDate() == null) {
                        return 1;
                    } else if (loan2.getReturnDate() == null) {
                        return -1;
                    } else {
                        return loan1.getReturnDate().compareTo(loan2.getReturnDate());
                    }
                };
                break;
            case BOOK_TITLE:
                comparator = Comparator.comparing(loan -> loan.getBook().getTitle());
                break;
            case BOOK_AUTHOR:
                comparator = Comparator.comparing(loan -> loan.getBook().getAuthor());
                break;
            case BOOK_ISBN:
                comparator = Comparator.comparing(loan -> loan.getBook().getIsbn());
                break;
            case STATUS:
                comparator = (loan1, loan2) -> {
                    boolean isOverdue1 = loan1.getReturnDate() == null && 
                                         loan1.getDueDate().isBefore(LocalDate.now());
                    boolean isOverdue2 = loan2.getReturnDate() == null && 
                                         loan2.getDueDate().isBefore(LocalDate.now());
                    boolean isReturned1 = loan1.getReturnDate() != null;
                    boolean isReturned2 = loan2.getReturnDate() != null;
                    
                    if (isOverdue1 && !isOverdue2) return 1;
                    if (!isOverdue1 && isOverdue2) return -1;
                    if (isReturned1 && !isReturned2) return -1;
                    if (!isReturned1 && isReturned2) return 1;
                    return 0;
                };
                break;
            default:
                comparator = Comparator.comparing(Loan::getLoanDate);
        }
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        loans.sort(comparator);
        
        System.out.println("\nLoan Records (Sorted by " + criteria.getDisplayName() + 
                           ", " + (ascending ? "Ascending" : "Descending") + "):");
        
        if (loans.isEmpty()) {
            System.out.println("No loan records found.");
        } else {
            for (Loan loan : loans) {
                System.out.println(loan);
            }
        }
    }

    public void viewLoansSorted(LoanSortCriteria criteria, boolean ascending) {
        viewLoansSorted(criteria, ascending, false);
    }

    public void viewLoansSorted(LoanSortCriteria criteria) {
        viewLoansSorted(criteria, true, false);
    }

    public void viewCurrentLoansSorted(LoanSortCriteria criteria, boolean ascending) {
        viewLoansSorted(criteria, ascending, true);
    }

    public void viewCurrentLoansSorted(LoanSortCriteria criteria) {
        viewLoansSorted(criteria, true, true);
    }

    public void viewAllBooksSorted(SortCriteria criteria, boolean ascending) {
        List<Book> sortedBooks = bookList.getSortedBooks(criteria, ascending);
        
        System.out.println("\nLibrary Books (Sorted by " + criteria.getDisplayName() + 
                           ", " + (ascending ? "Ascending" : "Descending") + "):");
        
        if (sortedBooks.isEmpty()) {
            System.out.println("No books found.");
        } else {
            for (Book book : sortedBooks) {
                System.out.println(book);
            }
        }
    }

    public void viewAllBooksSorted(SortCriteria criteria) {
        viewAllBooksSorted(criteria, true);
    }

    public List<Book> searchAndSortBooks(String query, SearchCriteria searchCriteria, SortCriteria sortCriteria, boolean ascending) {
        return bookList.searchAndSortBooks(query, searchCriteria, sortCriteria, ascending);
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(bookList.getBooks().values());
    }

    public void sortBooks(SortCriteria sortCriteria, boolean ascending) {
        bookList.getSortedBooks(sortCriteria, ascending).forEach(book -> {
            bookList.clear();
            bookList.addBook(book);
        });
    }

    // Add methods for getting and setting user preferences
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    /**
     * 保存用户偏好设置
     */
    public void saveUserPreferences() {
        GeneralStorage.saveUserPreferences(USER_PREFS_FILE, userPreferences);
        System.out.println("User preferences saved successfully.");
    }

    /**
     * 更新图书排序偏好
     * @param criteria 排序条件
     * @param ascending 是否升序
     */
    public void updateBookSortPreferences(SortCriteria criteria, boolean ascending) {
        userPreferences.setDefaultBookSortCriteria(criteria);
        userPreferences.setDefaultSortAscending(ascending);
        saveUserPreferences();
    }

    /**
     * 更新借阅记录排序偏好
     * @param criteria 排序条件
     * @param ascending 是否升序
     */
    public void updateLoanSortPreferences(LoanSortCriteria criteria, boolean ascending) {
        userPreferences.setDefaultLoanSortCriteria(criteria);
        userPreferences.setDefaultSortAscending(ascending);
        saveUserPreferences();
    }

    /**
     * 更新搜索偏好
     * @param criteria 搜索条件
     */
    public void updateSearchPreferences(SearchCriteria criteria) {
        userPreferences.setDefaultSearchCriteria(criteria);
        saveUserPreferences();
    }

    /**
     * 使用默认偏好显示所有图书
     */
    public void viewAllBooksSortedWithPreferences() {
        viewAllBooksSorted(
            userPreferences.getDefaultBookSortCriteria(),
            userPreferences.isDefaultSortAscending()
        );
    }

    /**
     * 使用默认偏好显示所有借阅记录
     */
    public void viewLoansSortedWithPreferences() {
        viewLoansSorted(
            userPreferences.getDefaultLoanSortCriteria(),
            userPreferences.isDefaultSortAscending(),
            !userPreferences.isShowReturnedLoans()
        );
    }
} 