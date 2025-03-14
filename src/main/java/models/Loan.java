package models;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a loan for a book borrowed by a user.
 */
public class Loan implements Serializable {
    private String loanId;
    private User borrower;
    private Book book;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private boolean isReturned;

    public Loan(String loanId, User borrower, Book book, LocalDate loanDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.borrower = borrower;
        this.book = book;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.isReturned = false; // Default to not returned
        book.setStatus(BookStatus.CHECKED_OUT); // Mark book as borrowed
    }

    // Getters and setters
    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public User getBorrower() {
        return borrower;
    }

    public void setBorrower(User borrower) {
        this.borrower = borrower;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public void returnBook() {
        if (!isReturned) {
            isReturned = true;
            book.setStatus(BookStatus.AVAILABLE); // Mark book as available
            System.out.println("Book '" + book.getTitle() + "' returned by " + borrower.getName());
        } else {
            System.out.println("Book '" + book.getTitle() + "' was already returned.");
        }
    }

    public int checkOverdueDays(LocalDate date) {
        if (!isReturned && date.isAfter(dueDate)) {
            book.setStatus(BookStatus.OVERDUE); // Mark book as overdue
            System.out.println("Book '" + book.getTitle() + "' is overdue.");
        }
        if (date.isAfter(dueDate)) {
            return (int) (date.toEpochDay() - dueDate.toEpochDay());
        }
        return 0; // Not overdue
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
