package models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理借阅记录的类
 */
public class LoanList {
    private List<Loan> loans;
    
    public LoanList() {
        this.loans = new ArrayList<>();
    }
    
    /**
     * 添加借阅记录
     * @param loan 要添加的借阅记录
     */
    public void addLoan(Loan loan) {
        if (loan != null) {
            loans.add(loan);
        }
    }
    
    /**
     * 添加新的借阅记录
     * @param borrower 借阅者
     * @param book 借阅的图书
     * @return 创建的借阅记录
     */
    public Loan createLoan(User borrower, Book book) {
        if (borrower == null || book == null) {
            throw new IllegalArgumentException("Borrower and book cannot be null");
        }
        
        LocalDate loanDate = LocalDate.now();
        LocalDate dueDate = loanDate.plusDays(21); // 默认借阅期限为21天
        
        Loan loan = new Loan(borrower, book, loanDate, dueDate);
        addLoan(loan);
        return loan;
    }
    
    /**
     * 获取所有借阅记录
     * @return 借阅记录列表
     */
    public List<Loan> getAllLoans() {
        return new ArrayList<>(loans);
    }
    
    /**
     * 获取当前借出的图书的借阅记录
     * @return 当前借出图书的借阅记录列表
     */
    public List<Loan> getCurrentLoans() {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取已归还图书的借阅记录
     * @return 已归还图书的借阅记录列表
     */
    public List<Loan> getReturnedLoans() {
        return loans.stream()
                .filter(loan -> loan.getReturnDate() != null)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取逾期未还的借阅记录
     * @return 逾期未还的借阅记录列表
     */
    public List<Loan> getOverdueLoans() {
        LocalDate today = LocalDate.now();
        return loans.stream()
                .filter(loan -> loan.getReturnDate() == null && loan.getDueDate().isBefore(today))
                .collect(Collectors.toList());
    }
    
    /**
     * 获取按指定条件排序的借阅记录
     * @param criteria 排序条件
     * @param ascending 是否升序排列
     * @return 排序后的借阅记录列表
     */
    public List<Loan> getSortedLoans(LoanSortCriteria criteria, boolean ascending) {
        List<Loan> loanList = new ArrayList<>(loans);
        
        Comparator<Loan> comparator;
        
        switch (criteria) {
            case LOAN_DATE:
                comparator = Comparator.comparing(Loan::getLoanDate);
                break;
            case DUE_DATE:
                comparator = Comparator.comparing(Loan::getDueDate);
                break;
            case RETURN_DATE:
                // 对于 null 的归还日期，我们将其视为"最大"日期
                comparator = (loan1, loan2) -> {
                    if (loan1.getReturnDate() == null && loan2.getReturnDate() == null) {
                        return 0;
                    } else if (loan1.getReturnDate() == null) {
                        return 1;
                    } else if (loan2.getReturnDate() == null) {
                        return -1;
                    } else {
                        return loan1.getReturnDate().compareTo(loan2.getReturnDate());
                    }
                };
                break;
            case BOOK_TITLE:
                comparator = Comparator.comparing(loan -> loan.getBook().getTitle());
                break;
            case BOOK_AUTHOR:
                comparator = Comparator.comparing(loan -> loan.getBook().getAuthor());
                break;
            case BOOK_ISBN:
                comparator = Comparator.comparing(loan -> loan.getBook().getIsbn());
                break;
            case STATUS:
                comparator = (loan1, loan2) -> {
                    boolean isOverdue1 = loan1.getReturnDate() == null && 
                                         loan1.getDueDate().isBefore(LocalDate.now());
                    boolean isOverdue2 = loan2.getReturnDate() == null && 
                                         loan2.getDueDate().isBefore(LocalDate.now());
                    boolean isReturned1 = loan1.getReturnDate() != null;
                    boolean isReturned2 = loan2.getReturnDate() != null;
                    
                    if (isOverdue1 && !isOverdue2) return 1;
                    if (!isOverdue1 && isOverdue2) return -1;
                    if (isReturned1 && !isReturned2) return -1;
                    if (!isReturned1 && isReturned2) return 1;
                    return 0;
                };
                break;
            default:
                comparator = Comparator.comparing(Loan::getLoanDate);
        }
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        loanList.sort(comparator);
        return loanList;
    }
    
    /**
     * 获取按指定条件升序排序的借阅记录
     * @param criteria 排序条件
     * @return 排序后的借阅记录列表
     */
    public List<Loan> getSortedLoans(LoanSortCriteria criteria) {
        return getSortedLoans(criteria, true);
    }
} 