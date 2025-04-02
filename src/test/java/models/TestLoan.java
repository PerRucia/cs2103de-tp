package models;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

public class TestLoan {
    private User borrower;
    private Book book;
    private BookList bookList;
    private LocalDate loanDate;
    private LocalDate dueDate;
    private Loan loan;

    @BeforeEach
    void setUp() {
        borrower = new User(false);
        book = new Book("1234567890", "Effective Java", "Joshua Bloch");
        bookList = new BookList();
        bookList.addBook(book);
        loanDate = LocalDate.now();
        dueDate = loanDate.plusDays(14);
        
        // 使用新的构造函数
        loan = new Loan(borrower, book, loanDate, dueDate);
        
        // 手动设置图书状态为已借出
        bookList.loanBook(book);
    }

    @Test
    void testGetLoanId() {
        // 由于我们现在使用生成的ID，我们需要检查ID不为空
        Assertions.assertNotNull(loan.getLoanId());
        Assertions.assertTrue(loan.getLoanId().contains(book.getIsbn()));
    }

    @Test
    void testGetBorrower() {
        Assertions.assertEquals(borrower, loan.getBorrower());
    }

    @Test
    void testGetBook() {
        Assertions.assertEquals(book, loan.getBook());
    }

    @Test
    void testGetLoanDate() {
        Assertions.assertEquals(loanDate, loan.getLoanDate());
    }

    @Test
    void testGetDueDate() {
        Assertions.assertEquals(dueDate, loan.getDueDate());
    }

    @Test
    void testIsReturned() {
        Assertions.assertFalse(loan.isReturned());
    }

    @Test
    void testGetReturnDate() {
        Assertions.assertNull(loan.getReturnDate());
    }

    @Test
    void testRenewLoan() {
        LocalDate originalDueDate = loan.getDueDate();
        loan.renewLoan(0);
        Assertions.assertEquals(originalDueDate.plusDays(14), loan.getDueDate());
    }

    @Test
    void testReturnBook() {
        Assertions.assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
        
        // 使用新的无参数 returnBook 方法
        loan.returnBook();
        
        // 手动更新图书状态
        bookList.returnBook(book);
        
        Assertions.assertTrue(loan.isReturned());
        Assertions.assertNotNull(loan.getReturnDate());
        Assertions.assertEquals(BookStatus.AVAILABLE, book.getStatus());
    }

    @Test
    void testCheckOverdue() {
        LocalDate futureDate = dueDate.plusDays(1);
        int daysOverdue = loan.checkOverdue(futureDate, bookList);
        Assertions.assertEquals(1, daysOverdue);
        Assertions.assertEquals(BookStatus.OVERDUE, book.getStatus());
    }

    @Test
    void testCheckNotOverdue() {
        LocalDate pastDate = dueDate.minusDays(1);
        int daysOverdue = loan.checkOverdue(pastDate, bookList);
        Assertions.assertEquals(0, daysOverdue);
        Assertions.assertEquals(BookStatus.CHECKED_OUT, book.getStatus());
    }
}