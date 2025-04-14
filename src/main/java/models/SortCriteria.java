package models;

/**
 * Define sorting criteria for books
 */
public enum SortCriteria {
    ISBN("ISBN"),
    TITLE("Title"),
    AUTHOR("Author"),
    STATUS("Status");
    
    private final String displayName;
    
    SortCriteria(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Get sorting criteria from integer choice
     * @param choice User's integer choice
     * @return Corresponding sorting criteria, returns TITLE if choice is invalid
     */
    public static SortCriteria fromChoice(int choice) {
        switch (choice) {
            case 1:
                return TITLE;
            case 2:
                return AUTHOR;
            case 3:
                return ISBN;
            case 4:
                return STATUS;
            default:
                return TITLE; // Default sorting by title
        }
    }
} 