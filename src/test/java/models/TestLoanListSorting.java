package models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

public class TestLoanListSorting {
    private LoanList loanList;
    private User user1, user2;
    private Book book1, book2, book3;
    private Loan loan1, loan2, loan3;
    
    @BeforeEach
    void setUp() {
        loanList = new LoanList();
        
        // 创建用户
        user1 = new User(false);
        user2 = new User(true);
        
        // 创建图书
        book1 = new Book("9780134685991", "Effective Java", "Joshua Bloch");
        book2 = new Book("9780132350884", "Clean Code", "Robert C. Martin");
        book3 = new Book("9781449331818", "Learning JavaScript", "Ethan Brown");
        
        // 创建借阅记录，使用不同的日期
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate lastWeek = today.minusDays(7);
        
        loan1 = new Loan(user1, book1, lastWeek, lastWeek.plusDays(21));
        loan2 = new Loan(user1, book2, yesterday, yesterday.plusDays(21));
        loan3 = new Loan(user2, book3, today, today.plusDays(21));
        
        // 设置一个借阅记录为已归还
        loan1.returnBook();
        
        // 添加到借阅列表
        loanList.addLoan(loan1);
        loanList.addLoan(loan2);
        loanList.addLoan(loan3);
    }
    
    @Test
    void testSortByLoanDate() {
        List<Loan> sorted = loanList.getSortedLoans(LoanSortCriteria.LOAN_DATE);
        
        assertEquals(3, sorted.size());
        assertEquals(loan1, sorted.get(0)); // 最早借出
        assertEquals(loan2, sorted.get(1));
        assertEquals(loan3, sorted.get(2)); // 最近借出
    }
    
    @Test
    void testSortByDueDate() {
        List<Loan> sorted = loanList.getSortedLoans(LoanSortCriteria.DUE_DATE);
        
        assertEquals(3, sorted.size());
        assertEquals(loan1, sorted.get(0)); // 最早到期
        assertEquals(loan2, sorted.get(1));
        assertEquals(loan3, sorted.get(2)); // 最晚到期
    }
    
    @Test
    void testSortByReturnDate() {
        List<Loan> sorted = loanList.getSortedLoans(LoanSortCriteria.RETURN_DATE);
        
        assertEquals(3, sorted.size());
        assertEquals(loan1, sorted.get(0)); // 已归还
        // loan2 和 loan3 都未归还，顺序可能取决于实现
    }
    
    @Test
    void testSortByBookTitle() {
        List<Loan> sorted = loanList.getSortedLoans(LoanSortCriteria.BOOK_TITLE);
        
        assertEquals(3, sorted.size());
        assertEquals("Clean Code", sorted.get(0).getBook().getTitle());
        assertEquals("Effective Java", sorted.get(1).getBook().getTitle());
        assertEquals("Learning JavaScript", sorted.get(2).getBook().getTitle());
    }
    
    @Test
    void testSortDescending() {
        List<Loan> sorted = loanList.getSortedLoans(LoanSortCriteria.LOAN_DATE, false);
        
        assertEquals(3, sorted.size());
        assertEquals(loan3, sorted.get(0)); // 最近借出
        assertEquals(loan2, sorted.get(1));
        assertEquals(loan1, sorted.get(2)); // 最早借出
    }
    
    @Test
    void testGetCurrentLoans() {
        List<Loan> current = loanList.getCurrentLoans();
        
        assertEquals(2, current.size());
        assertTrue(current.contains(loan2));
        assertTrue(current.contains(loan3));
        assertFalse(current.contains(loan1)); // loan1 已归还
    }
    
    @Test
    void testGetReturnedLoans() {
        List<Loan> returned = loanList.getReturnedLoans();
        
        assertEquals(1, returned.size());
        assertTrue(returned.contains(loan1));
    }
} 