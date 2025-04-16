package models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a loan for a book borrowed by a user.
 */
public class Loan implements Serializable {
    private static final int DEFAULT_RENEWAL_DAYS = 14; // Default number of days for renewal
    private final String loanId;
    private final User borrower;
    private final Book book;
    private final LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean isReturned;

    /**
     * Create a new borrowing record
     * @param borrower borrower
     * @param book borrowed book
     * @param loanDate borrowing date
     * @param dueDate due date
     */
    public Loan(User borrower, Book book, LocalDate loanDate, LocalDate dueDate) {
        this.loanId = generateLoanId(borrower, book, loanDate);
        this.borrower = borrower;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.isReturned = false;
    }

    /**
     * Generate a borrowing ID
     */
    private String generateLoanId(User borrower, Book book, LocalDate loanDate) {
        return book.getIsbn() + "-" + loanDate.toString();
    }

    // loanID, borrower, book, loanDate should not be overwritten
    public String getLoanId() {
        return loanId;
    }

    public User getBorrower() {
        return borrower;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    private void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void renewLoan(int days) {
        int additionalDays = days == 0 ? DEFAULT_RENEWAL_DAYS : days;
        setDueDate(dueDate.plusDays(additionalDays));
        System.out.println("Loan renewed. New due date: " + dueDate);
    }

    /**
     * Return a book (BookList parameter is not required)
     */
    public void returnBook() {
        if (!isReturned) {
            isReturned = true;
            returnDate = LocalDate.now();
            System.out.println("Book '" + book.getTitle() + "' returned by " + (borrower.isAdmin() ? "admin" : "user"));
        } else {
            System.out.println("Book '" + book.getTitle() + "' already returned.");
        }
    }

    public int checkOverdue(LocalDate date, BookList bookList) {
        if (date.isAfter(dueDate) && !isReturned) {
            System.out.println("Loan " + loanId + " is overdue.");
            bookList.overdueBook(book);
            return (int) (date.toEpochDay() - dueDate.toEpochDay());
        }
        return 0;
    }

    @Override
    public String toString() {
        LocalDate today = LocalDate.now();
        boolean isOverdue = returnDate == null && dueDate.isBefore(today);
        String status = returnDate != null ? "Returned" : (isOverdue ? "Overdue" : "On Loan");
        
        return String.format("Loan: %s - '%s' by %s - Borrowed: %s, Due: %s%s - Status: %s",
                book.getIsbn(),
                book.getTitle(),
                book.getAuthor(),
                loanDate,
                dueDate,
                returnDate != null ? ", Returned: " + returnDate : "",
                status);
    }
}
