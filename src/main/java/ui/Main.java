package ui;

import models.User;
import service.LibraryService;
import utils.InputUtil;

public class Main {
    private static LibraryService libraryService;
    private static User currentUser;

    public static void main(String[] args) {
        libraryService = new LibraryService();
        
        try {
            login();
            runMainLoop();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            libraryService.saveData();
            InputUtil.close();
            System.out.println("Exiting... Data saved.");
        }
    }

    private static void login() {
        System.out.println("\nWelcome to Library Management System");
        boolean isAdmin = InputUtil.readYesNo("Login as administrator? (y/n): ");
        currentUser = new User(isAdmin);
        System.out.println("Logged in as " + (isAdmin ? "Administrator" : "User"));
    }

    private static void runMainLoop() {
        boolean running = true;
        while (running) {
            displayMenu();
            int choice = InputUtil.readInt("Choose an option: ");
            try {
                running = handleMenuChoice(choice);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayMenu() {
        System.out.println("\nLibrary Management System");
        for (MenuOption option : MenuOption.values()) {
            if (!option.isAdminOnly() || currentUser.isAdmin()) {
                System.out.printf("%d. %s%n", option.getChoice(), option.getDescription());
            }
        }
    }

    private static boolean handleMenuChoice(int choice) {
        MenuOption option = MenuOption.fromChoice(choice, currentUser.isAdmin());
        
        switch (option) {
            case VIEW_ALL_BOOKS:
                libraryService.viewAllBooks();
                break;
            case LOAN_BOOK:
                libraryService.loanBook(InputUtil.readString("Enter ISBN to loan: "));
                break;
            case RETURN_BOOK:
                libraryService.returnBook(InputUtil.readString("Enter ISBN to return: "));
                break;
            case ADD_BOOK:
                handleAddBook();
                break;
            case REMOVE_BOOK:
                libraryService.removeBook(InputUtil.readString("Enter ISBN to remove: "));
                break;
            case VIEW_LOANS:
                libraryService.viewLoans();
                break;
            case EXIT:
                return false;
        }
        return true;
    }

    private static void handleAddBook() {
        String isbn = InputUtil.readString("Enter ISBN: ");
        String title = InputUtil.readString("Enter Title: ");
        String author = InputUtil.readString("Enter Author: ");
        libraryService.addBook(isbn, title, author);
    }
}
