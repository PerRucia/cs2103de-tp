package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class TestLoan {
    private User borrower;
    private Book book;
    private BookList bookList;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private Loan loan;

    @BeforeEach
    void initFields() {
        borrower = new User(false);
        book = new Book("1234567890", "Effective Java", "Joshua Bloch");
        bookList = new BookList();
        bookList.addBook(book);

        loanDate = LocalDate.now();
        dueDate = loanDate.plusDays(14);
        loan = new Loan("L1", borrower, book, loanDate, dueDate, bookList);
    }

    /**
     * Test the constructor of the Loan class.
     * This test checks if the constructor initializes all fields correctly.
     */
    @Test
    void testLoanConstructor() {
        Assertions.assertEquals("L1", loan.getLoanId());
        Assertions.assertEquals(borrower, loan.getBorrower());
        Assertions.assertEquals(book, loan.getBook());
        Assertions.assertEquals(loanDate, loan.getLoanDate());
        Assertions.assertEquals(dueDate, loan.getDueDate());
        Assertions.assertFalse(loan.isReturned());
        Assertions.assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
    }

    /**
     * Test the renewLoan method of the Loan class.
     * This test checks if the due date is updated correctly when renewed.
     */
    @Test
    void testRenewLoan() {
        // Test renewing the loan with a specific number of days
        loan.renewLoan(7);
        Assertions.assertEquals(dueDate.plusDays(7), loan.getDueDate());

        // Test renewing the loan with the default number of days
        loan.renewLoan(0);
        Assertions.assertEquals(dueDate.plusDays(21), loan.getDueDate());
    }

    /**
     * Test the returnBook method of the Loan class.
     * This test checks if the book status is updated correctly when returned.
     */
    @Test
    void testReturnBook() {
        Assertions.assertFalse(loan.isReturned());
        Assertions.assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
        
        loan.returnBook(bookList);
        
        Assertions.assertTrue(loan.isReturned());
        Assertions.assertNotNull(loan.getReturnDate());
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }
}