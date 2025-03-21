package ui;

import models.*;
import storage.GeneralStorage;

import java.util.Scanner;

public class Main {
    private static final String DATABASE_FILE = "src/main/resources/bookDatabase.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static BookList bookList;
    private static User currentUser;

    public static void main(String[] args) {
        // Load books
        bookList = GeneralStorage.loadBookList(DATABASE_FILE);
        if (bookList == null) {
            bookList = new BookList();
            System.out.println("No books loaded, starting with an empty library.");
        }

        // Start with login
        login();

        boolean running = true;
        while (running) {
            showMenu();
            int choice = getChoice();

            try {
                running = handleMenuChoice(choice);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        // Save books before exit
        GeneralStorage.saveBookList(DATABASE_FILE, bookList);
        System.out.println("Exiting... Data saved.");
    }

    private static void login() {
        System.out.println("\nWelcome to Library Management System");
        System.out.print("Login as administrator? (y/n): ");
        String input = scanner.nextLine().trim().toLowerCase();
        boolean isAdmin = input.equals("y") || input.equals("yes");
        currentUser = new User(isAdmin);
        System.out.println("Logged in as " + (isAdmin ? "Administrator" : "User"));
    }

    private static void showMenu() {
        System.out.println("\nLibrary Management System");
        System.out.println("1. View all books");
        System.out.println("2. Loan a book");
        System.out.println("3. Return a book");
        if (currentUser.isAdmin()) {
            System.out.println("4. Add a book");
            System.out.println("5. Remove a book");
            System.out.println("6. View all loans");
            System.out.println("7. Exit");
        } else {
            System.out.println("4. Exit");
        }
        System.out.print("Choose an option: ");
    }

    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static boolean handleMenuChoice(int choice) {
        if (currentUser.isAdmin()) {
            return handleAdminChoice(choice);
        } else {
            return handleUserChoice(choice);
        }
    }

    private static boolean handleUserChoice(int choice) {
        switch (choice) {
            case 1:
                viewAllBooks();
                break;
            case 2:
                loanBook();
                break;
            case 3:
                returnBook();
                break;
            case 4:
                return false; // Exit
            default:
                System.out.println("Invalid choice. Try again.");
        }
        return true;
    }

    private static boolean handleAdminChoice(int choice) {
        switch (choice) {
            case 1:
                viewAllBooks();
                break;
            case 2:
                loanBook();
                break;
            case 3:
                returnBook();
                break;
            case 4:
                addBook();
                break;
            case 5:
                removeBook();
                break;
            case 6:
                viewLoans();
                break;
            case 7:
                return false; // Exit
            default:
                System.out.println("Invalid choice. Try again.");
        }
        return true;
    }

    private static void viewAllBooks() {
        System.out.println("\nBooks in Library:");
        bookList.getAllBooks().values().forEach(System.out::println);
    }

    private static void addBook() {
        if (!currentUser.isAdmin()) {
            System.out.println("Error: Only administrators can add books.");
            return;
        }

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine();
        System.out.print("Enter Author: ");
        String author = scanner.nextLine();

        Book book = new Book(isbn, title, author);
        try {
            bookList.addBook(book);
            System.out.println("Book added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    private static void removeBook() {
        if (!currentUser.isAdmin()) {
            System.out.println("Error: Only administrators can remove books.");
            return;
        }

        System.out.print("Enter ISBN to remove: ");
        String isbn = scanner.nextLine();

        Book book = bookList.getBook(isbn);
        if (book != null) {
            try {
                bookList.removeBook(book);
                System.out.println("Book removed successfully.");
            } catch (IllegalStateException e) {
                System.out.println("Error removing book: " + e.getMessage());
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void loanBook() {
        System.out.print("Enter ISBN to loan: ");
        String isbn = scanner.nextLine();

        Book book = bookList.getBook(isbn);
        if (book != null) {
            try {
                bookList.loanBook(book);
                System.out.println("Book loaned successfully.");
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void returnBook() {
        System.out.print("Enter ISBN to return: ");
        String isbn = scanner.nextLine();

        Book book = bookList.getBook(isbn);
        if (book != null) {
            try {
                bookList.returnBook(book);
                System.out.println("Book returned successfully.");
            } catch (IllegalStateException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } else {
            System.out.println("Book not found.");
        }
    }

    private static void viewLoans() {
        if (!currentUser.isAdmin()) {
            System.out.println("Error: Only administrators can view all loans.");
            return;
        }

        System.out.println("\nCurrently Loaned Books:");
        bookList.getAllBooks().values().stream()
                .filter(book -> book.getStatus() == BookStatus.CHECKED_OUT)
                .forEach(System.out::println);
    }
}
