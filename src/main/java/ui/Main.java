package ui;

import models.*;
import storage.GeneralStorage;

import java.util.Scanner;

public class Main {
    private static final String DATABASE_FILE = "src/main/resources/bookDatabase.txt";
    private static final Scanner scanner = new Scanner(System.in);
    private static BookList bookList;

    public static void main(String[] args) {
        bookList = GeneralStorage.loadBookList(DATABASE_FILE);
        if (bookList == null) {
            bookList = new BookList();
            System.out.println("No books loaded, starting with an empty library.");
        }

        boolean running = true;
        while (running) {
            showMenu();
            int choice = getChoice();

            switch (choice) {
            case 1:
                viewAllBooks();
                break;
            case 2:
                addBook();
                break;
            case 3:
                removeBook();
                break;
            case 4:
                loanBook();
                break;
            case 5:
                returnBook();
                break;
            case 6:
                running = false;
                GeneralStorage.saveBookList(DATABASE_FILE, bookList);
                System.out.println("Exiting... Data saved.");
                break;
            default:
                System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void showMenu() {
        System.out.println("\nLibrary Management System");
        System.out.println("1. View all books");
        System.out.println("2. Add a book");
        System.out.println("3. Remove a book");
        System.out.println("4. Loan a book");
        System.out.println("5. Return a book");
        System.out.println("6. Exit");
        System.out.print("Choose an option: ");
    }

    private static int getChoice() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void viewAllBooks() {
        System.out.println("\nBooks in Library:");
        bookList.getAllBooks().values().forEach(System.out::println);
    }

    private static void addBook() {
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
        if (book != null && book.getStatus() == BookStatus.AVAILABLE) {
            bookList.loanBook(book);
            System.out.println("Book loaned successfully.");
        } else {
            System.out.println("Book not available for loan.");
        }
    }

    private static void returnBook() {
        System.out.print("Enter ISBN to return: ");
        String isbn = scanner.nextLine();

        Book book = bookList.getBook(isbn);
        if (book != null && book.getStatus() == BookStatus.CHECKED_OUT) {
            bookList.returnBook(book);
            System.out.println("Book returned successfully.");
        } else {
            System.out.println("Book is not currently loaned out.");
        }
    }
}
