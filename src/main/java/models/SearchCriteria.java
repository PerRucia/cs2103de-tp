package models;

/**
 * Define the conditions for book search
 */
public enum SearchCriteria {
    ALL("All Fields"),
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
     * Get search conditions from integer selection
     * @param choice The integer selected by the user
     * @return The corresponding search condition, if the selection is invalid, returns ALL
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
                return ALL; // Search all fields by default
        }
    }
} 