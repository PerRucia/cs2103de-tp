package models;

/**
 * Represents a librarian who has additional privileges in the library system.
 */
public class Librarian extends User {
    private String employeeId;

    public Librarian(String userId, String name, String email, String password, String employeeId) {
        super(userId, name, email, password);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public void addBook(Book book) {
        System.out.println("Librarian " + name + " added book: " + book.getTitle());
        book.setStatus(Book.BookStatus.AVAILABLE);
    }

    public void removeBook(Book book) {
        System.out.println("Librarian " + name + " removed book: " + book.getTitle());
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
