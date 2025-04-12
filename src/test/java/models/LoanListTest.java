package models;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoanListTest {

    private LoanList loanList;

    @BeforeEach
    public void setUp() {
        loanList = new LoanList();
    }

    @Test
    public void testAddLoan() {
        Loan loan = mock(Loan.class);
        when(loan.getReturnDate()).thenReturn(null);
        loanList.addLoan(loan);
        List<Loan> allLoans = loanList.getAllLoans();
        assertEquals(1, allLoans.size());
        assertSame(loan, allLoans.get(0));
    }

    @Test
    public void testCreateLoan() {
        User dummyUser = mock(User.class);
        Book dummyBook = mock(Book.class);
        Loan loan = loanList.createLoan(dummyUser, dummyBook);
        assertNotNull(loan);
        // Check that due date is 21 days after loan date
        LocalDate expectedDueDate = loan.getLoanDate().plusDays(21);
        assertEquals(expectedDueDate, loan.getDueDate());
        // Loan should have a null return date initially
        assertNull(loan.getReturnDate());
        assertTrue(loanList.getAllLoans().contains(loan));
    }

    @Test
    public void testGetCurrentLoans() {
        Loan currentLoan = mock(Loan.class);
        when(currentLoan.getReturnDate()).thenReturn(null);
        loanList.addLoan(currentLoan);
        List<Loan> currentLoans = loanList.getCurrentLoans();
        assertEquals(1, currentLoans.size());
        assertSame(currentLoan, currentLoans.get(0));
    }

    @Test
    public void testGetReturnedLoans() {
        Loan returnedLoan = mock(Loan.class);
        when(returnedLoan.getReturnDate()).thenReturn(LocalDate.now().minusDays(1));
        loanList.addLoan(returnedLoan);
        List<Loan> returnedLoans = loanList.getReturnedLoans();
        assertEquals(1, returnedLoans.size());
        assertSame(returnedLoan, returnedLoans.get(0));
    }

    @Test
    public void testGetOverdueLoans() {
        Loan overdueLoan = mock(Loan.class);
        // Simulate overdue: due date in the past and not yet returned
        when(overdueLoan.getReturnDate()).thenReturn(null);
        when(overdueLoan.getDueDate()).thenReturn(LocalDate.now().minusDays(1));
        loanList.addLoan(overdueLoan);
        List<Loan> overdueLoans = loanList.getOverdueLoans();
        assertEquals(1, overdueLoans.size());
        assertSame(overdueLoan, overdueLoans.get(0));
    }

    @Test
    public void testGetSortedLoansByLoanDate() {
        Loan loan1 = mock(Loan.class);
        Loan loan2 = mock(Loan.class);
        when(loan1.getLoanDate()).thenReturn(LocalDate.of(2023, 1, 1));
        when(loan2.getLoanDate()).thenReturn(LocalDate.of(2023, 2, 1));
        when(loan1.getReturnDate()).thenReturn(null);
        when(loan2.getReturnDate()).thenReturn(null);
        // Add in unsorted order
        loanList.addLoan(loan2);
        loanList.addLoan(loan1);
        List<Loan> sortedLoans = loanList.getSortedLoans(LoanSortCriteria.LOAN_DATE, true);
        assertEquals(loan1, sortedLoans.get(0));
        assertEquals(loan2, sortedLoans.get(1));
    }

    @Test
    public void testGetSortedLoansByDueDateDescending() {
        Loan loan1 = mock(Loan.class);
        Loan loan2 = mock(Loan.class);
        when(loan1.getDueDate()).thenReturn(LocalDate.of(2023, 3, 1));
        when(loan2.getDueDate()).thenReturn(LocalDate.of(2023, 2, 1));
        when(loan1.getLoanDate()).thenReturn(LocalDate.now());
        when(loan2.getLoanDate()).thenReturn(LocalDate.now());
        when(loan1.getReturnDate()).thenReturn(null);
        when(loan2.getReturnDate()).thenReturn(null);
        loanList.addLoan(loan1);
        loanList.addLoan(loan2);
        List<Loan> sortedLoans = loanList.getSortedLoans(LoanSortCriteria.DUE_DATE, false);
        // In descending order, loan1 (later due date) should come first.
        assertEquals(loan1, sortedLoans.get(0));
        assertEquals(loan2, sortedLoans.get(1));
    }

    @Test
    public void testGetSortedLoansByReturnDate() {
        Loan loan1 = mock(Loan.class);
        Loan loan2 = mock(Loan.class);
        // loan1 has a non-null return date while loan2 is not returned yet
        when(loan1.getReturnDate()).thenReturn(LocalDate.of(2023, 3, 1));
        when(loan2.getReturnDate()).thenReturn(null);
        when(loan1.getLoanDate()).thenReturn(LocalDate.now());
        when(loan2.getLoanDate()).thenReturn(LocalDate.now());
        loanList.addLoan(loan2);
        loanList.addLoan(loan1);
        List<Loan> sortedLoans = loanList.getSortedLoans(LoanSortCriteria.RETURN_DATE, true);
        // Non-null return date is considered "smaller" than null.
        assertEquals(loan1, sortedLoans.get(0));
        assertEquals(loan2, sortedLoans.get(1));
    }

    @Test
    public void testGetSortedLoansByBookTitle() {
        Loan loan1 = mock(Loan.class);
        Loan loan2 = mock(Loan.class);
        Book book1 = mock(Book.class);
        Book book2 = mock(Book.class);
        when(book1.getTitle()).thenReturn("A Tale");
        when(book2.getTitle()).thenReturn("Z Tale");
        when(loan1.getBook()).thenReturn(book1);
        when(loan2.getBook()).thenReturn(book2);
        when(loan1.getLoanDate()).thenReturn(LocalDate.now());
        when(loan2.getLoanDate()).thenReturn(LocalDate.now());
        when(loan1.getReturnDate()).thenReturn(null);
        when(loan2.getReturnDate()).thenReturn(null);
        // Add in reverse order
        loanList.addLoan(loan2);
        loanList.addLoan(loan1);
        List<Loan> sortedLoans = loanList.getSortedLoans(LoanSortCriteria.BOOK_TITLE, true);
        assertEquals(loan1, sortedLoans.get(0));
        assertEquals(loan2, sortedLoans.get(1));
    }

    @Test
    public void testGetSortedLoansByStatus() {
        // Create three loans: overdue, returned, and current.
        Loan overdueLoan = mock(Loan.class);
        Loan returnedLoan = mock(Loan.class);
        Loan currentLoan = mock(Loan.class);

        // Overdue: return date null and due date before today.
        when(overdueLoan.getReturnDate()).thenReturn(null);
        when(overdueLoan.getDueDate()).thenReturn(LocalDate.now().minusDays(5));
        // Returned: non-null return date.
        when(returnedLoan.getReturnDate()).thenReturn(LocalDate.now().minusDays(2));
        when(returnedLoan.getDueDate()).thenReturn(LocalDate.now().plusDays(1));
        // Current: return date null and due date after today.
        when(currentLoan.getReturnDate()).thenReturn(null);
        when(currentLoan.getDueDate()).thenReturn(LocalDate.now().plusDays(5));
        // For sorting consistency:
        when(overdueLoan.getLoanDate()).thenReturn(LocalDate.now().minusDays(10));
        when(returnedLoan.getLoanDate()).thenReturn(LocalDate.now().minusDays(8));
        when(currentLoan.getLoanDate()).thenReturn(LocalDate.now().minusDays(6));

        loanList.addLoan(overdueLoan);
        loanList.addLoan(returnedLoan);
        loanList.addLoan(currentLoan);

        List<Loan> sortedLoans = loanList.getSortedLoans(LoanSortCriteria.STATUS, true);
        // Expected order: returned first, then current, then overdue.
        assertEquals(returnedLoan, sortedLoans.get(0));
        assertEquals(currentLoan, sortedLoans.get(1));
        assertEquals(overdueLoan, sortedLoans.get(2));
    }
}
