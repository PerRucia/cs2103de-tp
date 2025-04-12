package models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class LoanSortCriteriaTest {

    @Test
    public void testValidChoices() {
        assertEquals(LoanSortCriteria.LOAN_DATE, LoanSortCriteria.fromChoice(1));
        assertEquals(LoanSortCriteria.DUE_DATE, LoanSortCriteria.fromChoice(2));
        assertEquals(LoanSortCriteria.RETURN_DATE, LoanSortCriteria.fromChoice(3));
        assertEquals(LoanSortCriteria.BOOK_TITLE, LoanSortCriteria.fromChoice(4));
        assertEquals(LoanSortCriteria.BOOK_AUTHOR, LoanSortCriteria.fromChoice(5));
        assertEquals(LoanSortCriteria.BOOK_ISBN, LoanSortCriteria.fromChoice(6));
        assertEquals(LoanSortCriteria.STATUS, LoanSortCriteria.fromChoice(7));
    }

    @Test
    public void testInvalidChoiceReturnsDefault() {
        // For invalid choices, the default should be LOAN_DATE.
        assertEquals(LoanSortCriteria.LOAN_DATE, LoanSortCriteria.fromChoice(0));
        assertEquals(LoanSortCriteria.LOAN_DATE, LoanSortCriteria.fromChoice(8));
        assertEquals(LoanSortCriteria.LOAN_DATE, LoanSortCriteria.fromChoice(-1));
    }

    @Test
    public void testDisplayName() {
        assertEquals("Loan Date", LoanSortCriteria.LOAN_DATE.getDisplayName());
        assertEquals("Due Date", LoanSortCriteria.DUE_DATE.getDisplayName());
        assertEquals("Return Date", LoanSortCriteria.RETURN_DATE.getDisplayName());
        assertEquals("Book Title", LoanSortCriteria.BOOK_TITLE.getDisplayName());
        assertEquals("Book Author", LoanSortCriteria.BOOK_AUTHOR.getDisplayName());
        assertEquals("Book ISBN", LoanSortCriteria.BOOK_ISBN.getDisplayName());
        assertEquals("Status", LoanSortCriteria.STATUS.getDisplayName());
    }
}
