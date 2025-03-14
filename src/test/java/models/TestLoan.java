package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class TestLoan {
    private User borrower;
    private Book book;

    @BeforeEach
    void initFields() {
        borrower = new User("U1", "Alice", "alice@example.com",
                "Secret123");
        book = new Book("1234567890", "Effective Java", "Joshua Bloch");
    }

    /**
     * Test the constructor of the Loan class.
     * This test checks if the constructor initializes all fields correctly.
     */
    @Test
    void testLoanConstructor() {
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);

        Loan loan = new Loan("L1", borrower, book, loanDate, dueDate);

        Assertions.assertEquals("L1", loan.getLoanId());
        Assertions.assertEquals(borrower, loan.getBorrower());
        Assertions.assertEquals(book, loan.getBook());
        Assertions.assertEquals(loanDate, loan.getLoanDate());
        Assertions.assertEquals(dueDate, loan.getDueDate());
        Assertions.assertFalse(loan.isReturned());
        Assertions.assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
    }

    /**
     * Test the setters of the Loan class.
     * This test checks if the setters work correctly.
     */
    @Test
    void testLoanSetters() {
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);

        Loan loan = new Loan("L1", borrower, book, loanDate, dueDate);

        loan.setLoanId("L2");
        loan.setBorrower(new User("U2", "Bob", "bob@example.com",
                "Secret321"));
        loan.setBook(new Book("0987654321", "Java Concurrency in Practice",
                "Brian Goetz"));
        loan.setLoanDate(loanDate.plusDays(1));
        loan.setDueDate(dueDate.plusDays(1));

        Assertions.assertEquals("L2", loan.getLoanId());
        Assertions.assertEquals("U2", loan.getBorrower().getUserId());
        Assertions.assertEquals("Bob", loan.getBorrower().getName());
        Assertions.assertEquals("0987654321", loan.getBook().getIsbn());
        Assertions.assertEquals("Java Concurrency in Practice", loan.getBook().getTitle());
        Assertions.assertEquals("Brian Goetz", loan.getBook().getAuthor());
        Assertions.assertEquals(loanDate.plusDays(1), loan.getLoanDate());

    }

    /**
     * Test the returnBook method of the Loan class.
     * This test checks if the book status is updated correctly when returned.
     */
    @Test
    void testReturnBook() {
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);

        Loan loan = new Loan("L1", borrower, book, loanDate, dueDate);
        loan.returnBook();

        Assertions.assertTrue(loan.isReturned());
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }

    /**
     * Test the returnBook method when called multiple times.
     * This test checks if the book status remains unchanged after multiple returns.
     */
    @Test
    void testRepeatedReturns() {
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);

        Loan loan = new Loan("L1", borrower, book, loanDate, dueDate);
        loan.returnBook();
        boolean initialReturnState = loan.isReturned();
        BookStatus initialBookStatus = book.getStatus();

        loan.returnBook();
        Assertions.assertEquals(initialReturnState, loan.isReturned());
        Assertions.assertEquals(initialBookStatus, book.getStatus());
    }

    /**
     * Test the checkOverdueDays method of the Loan class.
     * This test checks if the overdue days are calculated correctly.
     */
    @Test
    void testOverdueCheck() {
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);
        Loan loan = new Loan("L1", borrower, book, loanDate, dueDate);

        Assertions.assertEquals(0, loan.checkOverdueDays(loanDate));

        LocalDate overdueDate = loanDate.plusDays(20);
        Assertions.assertTrue(loan.checkOverdueDays(overdueDate) > 0);
        Assertions.assertEquals(BookStatus.OVERDUE, book.getStatus());
    }

    /**
     * Test the checkOverdueDays method when the book is returned.
     * This test checks if the overdue days are not counted after the book is returned.
     */
    @Test
    void testToString() {
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(14);

        Loan loan = new Loan("L1", borrower, book, loanDate, dueDate);
        String loanString = loan.toString();

        Assertions.assertTrue(loanString.contains("L1"));
        Assertions.assertTrue(loanString.contains(borrower.getName()));
        Assertions.assertTrue(loanString.contains(book.getTitle()));
        Assertions.assertTrue(loanString.contains(loanDate.toString()));
        Assertions.assertTrue(loanString.contains(dueDate.toString()));
    }
}