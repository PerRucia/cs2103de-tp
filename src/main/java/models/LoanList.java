package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for managing loan records
 */
public class LoanList {
    private List<Loan> loans;
    
    public LoanList() {
        this.loans = new ArrayList<>();
    }
    
    /**
     * Add a loan record
     * @param loan Loan record to add
     */
    public void addLoan(Loan loan) {
        if (loan != null) {
            loans.add(loan);
        }
    }
    
    /**
     * Create a new loan record
     * @param borrower Borrower
     * @param book Book being borrowed
     * @return Created loan record
     */
    public Loan createLoan(User borrower, Book book) {
        if (borrower == null || book == null) {
            throw new IllegalArgumentException("Borrower and book cannot be null");
        }
        
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(21); // Default loan period is 21 days
        
        Loan loan = new Loan(borrower, book, loanDate, dueDate);
        addLoan(loan);
        return loan;
    }
    
    /**
     * Get all loan records
     * @return List of loan records
     */
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }
    
    /**
     * Get loan records for currently borrowed books
     * @return List of loan records for currently borrowed books
     */
    public List<Loan> getCurrentLoans() {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toList());
    }
    
    /**
     * Get loan records for returned books
     * @return List of loan records for returned books
     */
    public List<Loan> getReturnedLoans() {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() != null)
                .collect(Collectors.toList());
    }
    
    /**
     * Get overdue loan records
     * @return List of overdue loan records
     */
    public List<Loan> getOverdueLoans() {
        LocalDate today = LocalDate.now();
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null && loan.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }
    
    /**
     * Get loan records sorted by specified criteria
     * @param criteria Sorting criteria
     * @param ascending Whether to sort in ascending order
     * @return Sorted list of loan records
     */
    public List<Loan> getSortedLoans(LoanSortCriteria criteria, boolean ascending) {
        List<Loan> loanList = new ArrayList<>(loans);
        
        Comparator<Loan> comparator;
        
        switch (criteria) {
            case LOAN_DATE:
                comparator = Comparator.comparing(Loan::getLoanDate);
                break;
            case DUE_DATE:
                comparator = Comparator.comparing(Loan::getDueDate);
                break;
            case RETURN_DATE:
                // For null return dates, we treat them as the "maximum" date
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
        
        loanList.sort(comparator);
        return loanList;
    }
    
    /**
     * Get loan records sorted by specified criteria in ascending order
     * @param criteria Sorting criteria
     * @return Sorted list of loan records
     */
    public List<Loan> getSortedLoans(LoanSortCriteria criteria) {
        return getSortedLoans(criteria, true);
    }
} 