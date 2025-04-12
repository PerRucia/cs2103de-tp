package models;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class UserPreferencesTest {

    @Test
    void testDefaultValues() {
        UserPreferences prefs = new UserPreferences();
        assertEquals(SortCriteria.TITLE, prefs.getDefaultBookSortCriteria());
        assertEquals(LoanSortCriteria.LOAN_DATE, prefs.getDefaultLoanSortCriteria());
        assertEquals(SearchCriteria.ALL, prefs.getDefaultSearchCriteria());
        assertTrue(prefs.isDefaultSortAscending());
        assertEquals(10, prefs.getItemsPerPage());
        assertTrue(prefs.isShowBookStatus());
        assertTrue(prefs.isShowReturnedLoans());
    }

    @Test
    void testSetDefaultBookSortCriteria() {
        UserPreferences prefs = new UserPreferences();
        // Setting to the same value as no alternative provided
        prefs.setDefaultBookSortCriteria(SortCriteria.TITLE);
        assertEquals(SortCriteria.TITLE, prefs.getDefaultBookSortCriteria());
    }

    @Test
    void testSetDefaultLoanSortCriteria() {
        UserPreferences prefs = new UserPreferences();
        prefs.setDefaultLoanSortCriteria(LoanSortCriteria.LOAN_DATE);
        assertEquals(LoanSortCriteria.LOAN_DATE, prefs.getDefaultLoanSortCriteria());
    }

    @Test
    void testSetDefaultSearchCriteria() {
        UserPreferences prefs = new UserPreferences();
        prefs.setDefaultSearchCriteria(SearchCriteria.ALL);
        assertEquals(SearchCriteria.ALL, prefs.getDefaultSearchCriteria());
    }

    @Test
    void testSetDefaultSortAscending() {
        UserPreferences prefs = new UserPreferences();
        prefs.setDefaultSortAscending(false);
        assertFalse(prefs.isDefaultSortAscending());
        prefs.setDefaultSortAscending(true);
        assertTrue(prefs.isDefaultSortAscending());
    }

    @Test
    void testSetItemsPerPage() {
        UserPreferences prefs = new UserPreferences();
        prefs.setItemsPerPage(20);
        assertEquals(20, prefs.getItemsPerPage());
        // Test that non-positive values do not change itemsPerPage
        prefs.setItemsPerPage(-5);
        assertEquals(20, prefs.getItemsPerPage());
        prefs.setItemsPerPage(0);
        assertEquals(20, prefs.getItemsPerPage());
    }

    @Test
    void testSetShowBookStatus() {
        UserPreferences prefs = new UserPreferences();
        prefs.setShowBookStatus(false);
        assertFalse(prefs.isShowBookStatus());
        prefs.setShowBookStatus(true);
        assertTrue(prefs.isShowBookStatus());
    }

    @Test
    void testSetShowReturnedLoans() {
        UserPreferences prefs = new UserPreferences();
        prefs.setShowReturnedLoans(false);
        assertFalse(prefs.isShowReturnedLoans());
        prefs.setShowReturnedLoans(true);
        assertTrue(prefs.isShowReturnedLoans());
    }
}

