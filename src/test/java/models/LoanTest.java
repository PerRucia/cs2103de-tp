package models;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoanTest {

    private models.Book mockBook;
    private models.User mockUser;
    private models.BookList mockBookList;
    private LocalDate loanDate;
    private LocalDate dueDate;

    @BeforeEach
    public void setUp() {
        // Create dummy objects using Mockito
        mockBook = mock(models.Book.class);
        when(mockBook.getIsbn()).thenReturn("1234567890");
        when(mockBook.getTitle()).thenReturn("Sample Book");
        when(mockBook.getAuthor()).thenReturn("Author Name");

        mockUser = mock(models.User.class);
        when(mockUser.isAdmin()).thenReturn(false);

        mockBookList = mock(models.BookList.class);

        loanDate = LocalDate.of(2023, 1, 1);
        dueDate = LocalDate.of(2023, 1, 15);
    }

    @Test
    public void testLoanCreationAndLoanIdGeneration() {
        Loan loan = new Loan(mockUser, mockBook, loanDate, dueDate);
        // loanId is based on isbn and loanDate
        assertEquals("1234567890-" + loanDate.toString(), loan.getLoanId());
        assertEquals(mockUser, loan.getBorrower());
        assertEquals(mockBook, loan.getBook());
        assertEquals(loanDate, loan.getLoanDate());
        assertEquals(dueDate, loan.getDueDate());
        assertFalse(loan.isReturned());
        assertNull(loan.getReturnDate());
    }

    @Test
    public void testRenewLoanWithDefaultDays() {
        Loan loan = new Loan(mockUser, mockBook, loanDate, dueDate);
        loan.renewLoan(0); // default renewal days should be applied
        assertEquals(dueDate.plusDays(14), loan.getDueDate());
    }

    @Test
    public void testRenewLoanWithCustomDays() {
        Loan loan = new Loan(mockUser, mockBook, loanDate, dueDate);
        loan.renewLoan(7);
        assertEquals(dueDate.plusDays(7), loan.getDueDate());
    }

    @Test
    public void testReturnBook() {
        Loan loan = new Loan(mockUser, mockBook, loanDate, dueDate);
        // Initially, not returned.
        assertFalse(loan.isReturned());
        assertNull(loan.getReturnDate());
        loan.returnBook();
        assertTrue(loan.isReturned());
        assertNotNull(loan.getReturnDate());
        LocalDate firstReturnDate = loan.getReturnDate();
        // Calling again should not change returnDate.
        loan.returnBook();
        assertEquals(firstReturnDate, loan.getReturnDate());
    }

    @Test
    public void testCheckOverdueWhenOverdue() {
        // Create a loan that is overdue
        LocalDate pastDueDate = LocalDate.of(2023, 1, 10);
        Loan loan = new Loan(mockUser, mockBook, loanDate, pastDueDate);
        LocalDate testDate = LocalDate.of(2023, 1, 15);
        int overdueDays = loan.checkOverdue(testDate, mockBookList);
        // Verify overdueBook method was called on BookList
        verify(mockBookList).overdueBook(mockBook);
        assertEquals((int)(testDate.toEpochDay() - pastDueDate.toEpochDay()), overdueDays);
    }

    @Test
    public void testCheckOverdueWhenNotOverdue() {
        Loan loan = new Loan(mockUser, mockBook, loanDate, dueDate);
        LocalDate testDate = LocalDate.of(2023, 1, 10);
        int overdueDays = loan.checkOverdue(testDate, mockBookList);
        // Verify that overdueBook was not called
        verify(mockBookList, never()).overdueBook(any());
        assertEquals(0, overdueDays);
    }

    @Test
    public void testToString() {
        Loan loan = new Loan(mockUser, mockBook, loanDate, dueDate);
        String str = loan.toString();
        System.out.println(str);
        // Validate that key components appear in the string.
        assertTrue(str.contains("1234567890"));
        assertTrue(str.contains("Sample Book"));
        assertTrue(str.contains("Author Name"));
        assertTrue(str.contains(loanDate.toString()));
        assertTrue(str.contains(dueDate.toString()));
        // For a non-returned and non overdue loan, status should be "On Loan".
        assertTrue(str.contains("Overdue"));
    }
}
