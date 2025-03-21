package service;

import models.*;
import storage.GeneralStorage;

public class LibraryService {
    private static final String DATABASE_FILE = "src/main/resources/bookDatabase.txt";
    private final BookList bookList;

    public LibraryService() {
        this.bookList = GeneralStorage.loadBookList(DATABASE_FILE);
        if (this.bookList == null) {
            this.bookList = new BookList();
        }
    }

    public void saveData() {
        GeneralStorage.saveBookList(DATABASE_FILE, bookList);
    }

    public void viewAllBooks() {
        System.out.println("\nBooks in Library:");
        bookList.getAllBooks().values().forEach(System.out::println);
    }

    public void addBook(String isbn, String title, String author) {
        Book book = new Book(isbn, title, author);
        try {
            bookList.addBook(book);
            System.out.println("Book added successfully.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public void removeBook(String isbn) {
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

    public void loanBook(String isbn) {
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

    public void returnBook(String isbn) {
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

    public void viewLoans() {
        System.out.println("\nCurrently Loaned Books:");
        bookList.getAllBooks().values().stream()
                .filter(book -> book.getStatus() == BookStatus.CHECKED_OUT)
                .forEach(System.out::println);
    }
} 