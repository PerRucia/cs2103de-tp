package models;

/**
 * 定义图书搜索的条件
 */
public enum SearchCriteria {
    TITLE("Title"),
    AUTHOR("Author"),
    ISBN("ISBN"),
    ALL_FIELDS("All Fields");
    
    private final String displayName;
    
    SearchCriteria(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 从整数选择获取搜索条件
     * @param choice 用户选择的整数
     * @return 对应的搜索条件，如果选择无效则返回ALL_FIELDS
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
                return ALL_FIELDS;
            default:
                return ALL_FIELDS; // 默认搜索所有字段
        }
    }
} 