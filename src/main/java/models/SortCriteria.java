package models;

/**
 * Define the conditions for sorting books
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
     * Get sorting conditions from integer selection
     * @param choice The integer selected by the user
     * @return The corresponding sorting condition, if the selection is invalid, returns TITLE
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
                return TITLE; // Default sorting is by title
        }
    }
} 