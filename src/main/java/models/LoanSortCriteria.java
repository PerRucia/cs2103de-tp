package models;

/**
 * Define the conditions for sorting borrowing records
 */
public enum LoanSortCriteria {
    LOAN_DATE("Loan Date"),
    DUE_DATE("Due Date"),
    RETURN_DATE("Return Date"),
    BOOK_TITLE("Book Title"),
    BOOK_AUTHOR("Book Author"),
    BOOK_ISBN("Book ISBN"),
    STATUS("Status");
    
    private final String displayName;
    
    LoanSortCriteria(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get sorting conditions from integer selection
     * @param choice The integer selected by the user
     * @return The corresponding sorting condition, if the selection is invalid, return LOAN_DATE
     */
    public static LoanSortCriteria fromChoice(int choice) {
        switch (choice) {
            case 1:
                return LOAN_DATE;
            case 2:
                return DUE_DATE;
            case 3:
                return RETURN_DATE;
            case 4:
                return BOOK_TITLE;
            case 5:
                return BOOK_AUTHOR;
            case 6:
                return BOOK_ISBN;
            case 7:
                return STATUS;
            default:
                return LOAN_DATE; // Default sorting by borrowing date
        }
    }
} 