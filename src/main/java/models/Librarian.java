package models;

/**
 * Represents a librarian who has additional privileges in the library system.
 */
public class Librarian extends User {
    private String employeeId;
    private BookList bookList;

    public Librarian(String userId, String name, String email, String password, String employeeId,
                     BookList bookList) {
        super(userId, name, email, password);
        this.employeeId = employeeId;
        this.bookList = bookList;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void addBook(Book book) {
        try {
            bookList.addBook(book);
            System.out.println("Librarian " + name + " added book: " + book.getTitle());
        } catch (IllegalArgumentException e) {
            System.out.println("Cannot add book: " + e.getMessage());
        }

    }

    public void removeBook(Book book) {
        try {
            bookList.removeBook(book);
            System.out.println("Librarian " + name + " removed book: " + book.getTitle());
        } catch (IllegalStateException e) {
            System.out.println("Cannot remove book: " + e.getMessage());
        }
    }

    public void manageUsers() {
        System.out.println("Librarian " + name + " is managing library users.");
    }

    @Override
    public String toString() {
        return "Librarian{" +
                "employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
