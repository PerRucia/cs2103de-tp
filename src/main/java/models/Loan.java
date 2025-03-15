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

    public Loan(String loanId, User borrower, Book book, LocalDate loanDate, LocalDate dueDate,
                BookList bookList) {
        this.loanId = loanId;
        this.borrower = borrower;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.returnDate = null;
        this.isReturned = false; // Default to not returned
        bookList.loanBook(book); // Mark the book as loaned
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

    public void returnBook(BookList bookList) {
        if (!isReturned) {
            isReturned = true;
            bookList.returnBook(book);
            returnDate = LocalDate.now();
            System.out.println("Book '" + book.getTitle() + "' returned by " + borrower.getName());
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
        return "Loan{" +
                "loanId='" + loanId + '\'' +
                ", borrower=" + borrower.getName() +
                ", book=" + book.getTitle() +
                ", loanDate=" + loanDate +
                ", dueDate=" + dueDate +
                ", isReturned=" + isReturned +
                '}';
    }
}
