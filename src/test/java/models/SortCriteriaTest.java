package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SortCriteriaTest {

    @Test
    void testGetDisplayNameAndToString() {
        assertEquals("ISBN", SortCriteria.ISBN.getDisplayName());
        assertEquals("Title", SortCriteria.TITLE.getDisplayName());
        assertEquals("Author", SortCriteria.AUTHOR.getDisplayName());
        assertEquals("Status", SortCriteria.STATUS.getDisplayName());
        
        assertEquals("ISBN", SortCriteria.ISBN.toString());
        assertEquals("Title", SortCriteria.TITLE.toString());
        assertEquals("Author", SortCriteria.AUTHOR.toString());
        assertEquals("Status", SortCriteria.STATUS.toString());
    }

    @Test
    void testFromChoiceValid() {
        assertEquals(SortCriteria.TITLE,  SortCriteria.fromChoice(1));
        assertEquals(SortCriteria.AUTHOR, SortCriteria.fromChoice(2));
        assertEquals(SortCriteria.ISBN,   SortCriteria.fromChoice(3));
        assertEquals(SortCriteria.STATUS, SortCriteria.fromChoice(4));
    }
    
    @Test
    void testFromChoiceInvalid() {
        // For invalid choices, the default is TITLE.
        assertEquals(SortCriteria.TITLE, SortCriteria.fromChoice(0));
        assertEquals(SortCriteria.TITLE, SortCriteria.fromChoice(5));
    }
}
