package models;

/**
 * 定义图书搜索的条件
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
     * 从整数选择获取搜索条件
     * @param choice 用户选择的整数
     * @return 对应的搜索条件，如果选择无效则返回ALL
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
                return ALL; // 默认搜索所有字段
        }
    }
} 