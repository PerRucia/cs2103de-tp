package service;

import models.*;
import storage.GeneralStorage;
import java.util.List;
import java.util.Comparator;
import java.time.LocalDate;

public class LibraryService {
    private static final String DATABASE_FILE = "src/main/resources/bookDatabase.txt";
    private final BookList bookList;
    private final LoanList loanList;

    public LibraryService() {
        BookList bookList1;
        bookList1 = GeneralStorage.loadBookList(DATABASE_FILE);
        if (bookList1 == null) {
            bookList1 = new BookList();
        }
        this.bookList = bookList1;
        this.loanList = new LoanList();
    }

    public void saveData() {
        GeneralStorage.saveBookList(DATABASE_FILE, bookList);
    }

    public void viewAllBooks() {
        System.out.println("\nBooks in Library:");
        bookList.getAllBooks().values().forEach(System.out::println);
    }

    public void addBook(String isbn, String title, String author) {
        Book book = new Book(isbn, title, author);
        try {
            bookList.addBook(book);
            System.out.println("Book added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public void removeBook(String isbn) {
        Book book = bookList.getBook(isbn);
        if (book != null) {
            try {
                bookList.removeBook(book);
                System.out.println("Book removed successfully.");
            } catch (IllegalStateException e) {
                System.out.println("Error removing book: " + e.getMessage());
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    public void loanBook(String isbn) {
        Book book = bookList.getBook(isbn);
        if (book != null) {
            try {
                bookList.loanBook(book);
                User borrower = new User(false);
                loanList.createLoan(borrower, book);
                System.out.println("Book loaned successfully.");
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Book not found.");
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

    public void viewLoans() {
        List<Loan> currentLoans = loanList.getCurrentLoans();
        System.out.println("\nCurrently Loaned Books:");
        if (currentLoans.isEmpty()) {
            System.out.println("No books currently on loan.");
        } else {
            for (Loan loan : currentLoans) {
                System.out.println(loan);
            }
        }
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

    /**
     * 显示按指定条件排序的所有图书
     * @param criteria 排序条件
     * @param ascending 是否升序排列
     */
    public void viewAllBooksSorted(SortCriteria criteria, boolean ascending) {
        List<Book> sortedBooks = bookList.getSortedBooks(criteria, ascending);
        
        System.out.println("\nLibrary Books (Sorted by " + criteria.getDisplayName() + 
                           ", " + (ascending ? "Ascending" : "Descending") + "):");
        
        if (sortedBooks.isEmpty()) {
            System.out.println("No books in the library.");
        } else {
            for (Book book : sortedBooks) {
                System.out.println(book);
            }
        }
    }

    /**
     * 显示按指定条件升序排序的所有图书
     * @param criteria 排序条件
     */
    public void viewAllBooksSorted(SortCriteria criteria) {
        viewAllBooksSorted(criteria, true);
    }

    /**
     * 搜索图书并显示结果
     * @param query 查询字符串
     * @param criteria 搜索条件
     */
    public void searchBooks(String query, SearchCriteria criteria) {
        List<Book> results = bookList.searchBooks(query, criteria);
        
        System.out.println("\nSearch Results for '" + query + "' in " + criteria.getDisplayName() + ":");
        
        if (results.isEmpty()) {
            System.out.println("No books found matching your search criteria.");
        } else {
            System.out.println("Found " + results.size() + " book(s):");
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }

    /**
     * 搜索图书并对结果进行排序
     * @param query 查询字符串
     * @param searchCriteria 搜索条件
     * @param sortCriteria 排序条件
     * @param ascending 是否升序排列
     */
    public void searchAndSortBooks(String query, SearchCriteria searchCriteria, 
                                  SortCriteria sortCriteria, boolean ascending) {
        List<Book> results = bookList.searchAndSortBooks(query, searchCriteria, sortCriteria, ascending);
        
        System.out.println("\nSearch Results for '" + query + "' in " + searchCriteria.getDisplayName() + 
                           " (Sorted by " + sortCriteria.getDisplayName() + 
                           ", " + (ascending ? "Ascending" : "Descending") + "):");
        
        if (results.isEmpty()) {
            System.out.println("No books found matching your search criteria.");
        } else {
            System.out.println("Found " + results.size() + " book(s):");
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }

    /**
     * 按相关性搜索图书
     * @param query 查询字符串
     * @param criteria 搜索条件
     */
    public void searchBooksByRelevance(String query, SearchCriteria criteria) {
        List<Book> results = bookList.searchBooksByRelevance(query, criteria);
        
        System.out.println("\nSearch Results for '" + query + "' in " + criteria.getDisplayName() + 
                           " (Sorted by Relevance):");
        
        if (results.isEmpty()) {
            System.out.println("No books found matching your search criteria.");
        } else {
            System.out.println("Found " + results.size() + " book(s):");
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }
} 