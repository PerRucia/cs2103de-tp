package models;

/**
 * 定义借阅记录排序的条件
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
     * 从整数选择获取排序条件
     * @param choice 用户选择的整数
     * @return 对应的排序条件，如果选择无效则返回LOAN_DATE
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
                return LOAN_DATE; // 默认按借阅日期排序
        }
    }
} 