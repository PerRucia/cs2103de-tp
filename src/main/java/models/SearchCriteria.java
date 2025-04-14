package models;

/**
 * Define search criteria for books
 */
public enum SearchCriteria {
    ALL("All"),
    ISBN("ISBN"),
    TITLE("Title"),
    AUTHOR("Author"),
    STATUS("Status");
    
    private final String displayName;
    
    SearchCriteria(String displayName) {
        this.displayName = displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
    
    /**
     * Get search criteria from integer choice
     * @param choice User's integer choice
     * @return Corresponding search criteria, returns ALL if choice is invalid
     */
    public static SearchCriteria fromChoice(int choice) {
        switch (choice) {
            case 1:
                return TITLE;
            case 2:
                return AUTHOR;
            case 3:
                return ISBN;
            case 4:
                return ALL;
            case 5:
                return STATUS;
            default:
                return ALL; // Default search all fields
        }
    }
} 