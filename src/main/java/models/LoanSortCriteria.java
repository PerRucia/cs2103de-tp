package models;

/**
 * Define sorting criteria for loan records
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
     * Get sorting criteria from integer choice
     * @param choice User's integer choice
     * @return Corresponding sorting criteria, returns LOAN_DATE if choice is invalid
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
                return LOAN_DATE; // Default sorting by loan date
        }
    }
} 