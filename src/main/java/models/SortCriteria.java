package models;

/**
 * 定义图书排序的条件
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
     * 从整数选择获取排序条件
     * @param choice 用户选择的整数
     * @return 对应的排序条件，如果选择无效则返回TITLE
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