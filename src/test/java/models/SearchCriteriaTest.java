package models;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SearchCriteriaTest {

    @Test
    void testToString() {
        assertEquals("All Fields", SearchCriteria.ALL.toString());
        assertEquals("ISBN", SearchCriteria.ISBN.toString());
        assertEquals("Title", SearchCriteria.TITLE.toString());
        assertEquals("Author", SearchCriteria.AUTHOR.toString());
        assertEquals("Status", SearchCriteria.STATUS.toString());
    }

    @Test
    void testFromChoiceValid() {
        assertEquals(SearchCriteria.TITLE, SearchCriteria.fromChoice(1));
        assertEquals(SearchCriteria.AUTHOR, SearchCriteria.fromChoice(2));
        assertEquals(SearchCriteria.ISBN, SearchCriteria.fromChoice(3));
        assertEquals(SearchCriteria.ALL,   SearchCriteria.fromChoice(4));
        assertEquals(SearchCriteria.STATUS, SearchCriteria.fromChoice(5));
    }
    
    @Test
    void testFromChoiceInvalid() {
        // For invalid choices, the default is ALL.
        assertEquals(SearchCriteria.ALL, SearchCriteria.fromChoice(0));
        assertEquals(SearchCriteria.ALL, SearchCriteria.fromChoice(6));
    }
}
