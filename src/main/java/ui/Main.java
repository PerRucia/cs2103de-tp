package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.User;
import service.LibraryService;
import utils.InputUtil;
import models.SortCriteria;
import models.LoanSortCriteria;
import models.SearchCriteria;
import models.UserPreferences;
import java.util.List;
import models.Book;

public class Main extends Application {
    private static LibraryService libraryService;
    private static User currentUser;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("Library Management System");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Suppress JavaFX TSM messages on macOS
        System.setProperty("javafx.macosx.enableDynamicCoreAnimation", "false");
        System.setProperty("apple.awt.UIElement", "true");
        
        libraryService = new LibraryService();
        
        try {
            launch(args);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            libraryService.saveData();
            InputUtil.close();
            System.out.println("Exiting... Data saved.");
        }
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    private static void login() {
        System.out.println("\nWelcome to Library Management System");
        boolean isAdmin = InputUtil.readYesNo("Login as administrator? (y/n): ");
        String userId = "default"; // In a real application, this would be entered by the user
        currentUser = new User(userId, isAdmin);
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
            case VIEW_SORTED_BOOKS:
                handleViewSortedBooks();
                break;
            case VIEW_SORTED_LOANS:
                handleViewSortedLoans();
                break;
            case SEARCH_BOOKS:
                handleSearchBooks();
                break;
            case USER_PREFERENCES:
                handleUserPreferences();
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

    private static void handleViewSortedBooks() {
        System.out.println("\nSort books by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.println("4. Status");
        
        int sortChoice = InputUtil.readInt("Choose sorting criteria: ");
        SortCriteria criteria = SortCriteria.fromChoice(sortChoice);
        
        System.out.println("\nSort direction:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        
        int directionChoice = InputUtil.readInt("Choose direction: ");
        boolean ascending = directionChoice != 2; // 1 or other values for ascending order, 2 for descending order
        
        libraryService.viewAllBooksSorted(criteria, ascending);
    }

    private static void handleViewSortedLoans() {
        System.out.println("\nSort loans by:");
        System.out.println("1. Loan Date");
        System.out.println("2. Due Date");
        System.out.println("3. Return Date");
        System.out.println("4. Book Title");
        System.out.println("5. Book Author");
        System.out.println("6. Book ISBN");
        System.out.println("7. Status");
        
        int sortChoice = InputUtil.readInt("Choose sorting criteria: ");
        LoanSortCriteria criteria = LoanSortCriteria.fromChoice(sortChoice);
        
        System.out.println("\nSort direction:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        
        int directionChoice = InputUtil.readInt("Choose direction: ");
        boolean ascending = directionChoice != 2; // 1 or other values for ascending order, 2 for descending order
        
        System.out.println("\nView options:");
        System.out.println("1. All loans");
        System.out.println("2. Current loans only");
        
        int viewChoice = InputUtil.readInt("Choose view option: ");
        boolean currentOnly = viewChoice == 2;
        
        if (currentOnly) {
            libraryService.viewCurrentLoansSorted(criteria, ascending);
        } else {
            libraryService.viewLoansSorted(criteria, ascending);
        }
    }

    private static void handleSearchBooks() {
        System.out.println("\nSearch books by:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.println("4. All Fields");
        
        int searchChoice = InputUtil.readInt("Choose search criteria: ");
        SearchCriteria searchCriteria = SearchCriteria.fromChoice(searchChoice);
        
        String query = InputUtil.readString("Enter search query: ");
        
        System.out.println("\nSort results by:");
        System.out.println("1. Relevance");
        System.out.println("2. Title");
        System.out.println("3. Author");
        System.out.println("4. ISBN");
        System.out.println("5. Status");
        
        int sortChoice = InputUtil.readInt("Choose sorting criteria: ");
        
        if (sortChoice == 1) {
            // Sort by relevance
            handleSearch(query, searchCriteria);
        } else {
            // Sort by other conditions
            SortCriteria sortCriteria;
            switch (sortChoice) {
                case 2:
                    sortCriteria = SortCriteria.TITLE;
                    break;
                case 3:
                    sortCriteria = SortCriteria.AUTHOR;
                    break;
                case 4:
                    sortCriteria = SortCriteria.ISBN;
                    break;
                case 5:
                    sortCriteria = SortCriteria.STATUS;
                    break;
                default:
                    sortCriteria = SortCriteria.TITLE;
            }
            
            System.out.println("\nSort direction:");
            System.out.println("1. Ascending");
            System.out.println("2. Descending");
            
            int directionChoice = InputUtil.readInt("Choose direction: ");
            boolean ascending = directionChoice != 2; // 1 or other values for ascending order, 2 for descending order
            
            libraryService.searchAndSortBooks(query, searchCriteria, sortCriteria, ascending);
        }
    }

    private static void handleSearch(String query, SearchCriteria searchCriteria) {
        List<Book> results = libraryService.searchAndSortBooks(query, searchCriteria, SortCriteria.TITLE, true);
        if (results.isEmpty()) {
            System.out.println("No books found matching your search criteria.");
        } else {
            System.out.println("Found " + results.size() + " book(s):");
            for (Book book : results) {
                System.out.println(book);
            }
        }
    }

    private static void handleUserPreferences() {
        System.out.println("\nUser Preferences:");
        System.out.println("1. Default book sort settings");
        System.out.println("2. Default loan sort settings");
        System.out.println("3. Default search settings");
        System.out.println("4. Display settings");
        System.out.println("5. Back to main menu");
        
        int choice = InputUtil.readInt("Choose an option: ");
        
        switch (choice) {
            case 1:
                handleBookSortPreferences();
                break;
            case 2:
                handleLoanSortPreferences();
                break;
            case 3:
                handleSearchPreferences();
                break;
            case 4:
                handleDisplayPreferences();
                break;
            case 5:
                return;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }

    private static void handleBookSortPreferences() {
        System.out.println("\nDefault Book Sort Criteria:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.println("4. Status");
        
        int sortChoice = InputUtil.readInt("Choose default sorting criteria: ");
        SortCriteria criteria = SortCriteria.fromChoice(sortChoice);
        
        System.out.println("\nDefault Sort Direction:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        
        int directionChoice = InputUtil.readInt("Choose default direction: ");
        boolean ascending = directionChoice != 2;
        
        libraryService.updateBookSortPreferences(criteria, ascending);
        System.out.println("Book sort preferences updated successfully.");
    }

    private static void handleLoanSortPreferences() {
        System.out.println("\nDefault Loan Sort Criteria:");
        System.out.println("1. Loan Date");
        System.out.println("2. Due Date");
        System.out.println("3. Return Date");
        System.out.println("4. Book Title");
        System.out.println("5. Book Author");
        System.out.println("6. Book ISBN");
        System.out.println("7. Status");
        
        int sortChoice = InputUtil.readInt("Choose default sorting criteria: ");
        LoanSortCriteria criteria = LoanSortCriteria.fromChoice(sortChoice);
        
        System.out.println("\nDefault Sort Direction:");
        System.out.println("1. Ascending");
        System.out.println("2. Descending");
        
        int directionChoice = InputUtil.readInt("Choose default direction: ");
        boolean ascending = directionChoice != 2;
        
        libraryService.updateLoanSortPreferences(criteria, ascending);
        System.out.println("Loan sort preferences updated successfully.");
    }

    private static void handleSearchPreferences() {
        System.out.println("\nDefault Search Criteria:");
        System.out.println("1. Title");
        System.out.println("2. Author");
        System.out.println("3. ISBN");
        System.out.println("4. All Fields");
        
        int searchChoice = InputUtil.readInt("Choose default search criteria: ");
        SearchCriteria criteria = SearchCriteria.fromChoice(searchChoice);
        
        libraryService.updateSearchPreferences(criteria);
        System.out.println("Search preferences updated successfully.");
    }

    private static void handleDisplayPreferences() {
        UserPreferences prefs = libraryService.getUserPreferences();
        
        System.out.println("\nDisplay Settings:");
        System.out.println("1. Show book status: " + (prefs.isShowBookStatus() ? "Yes" : "No"));
        System.out.println("2. Show returned loans: " + (prefs.isShowReturnedLoans() ? "Yes" : "No"));
        System.out.println("3. Back");
        
        int choice = InputUtil.readInt("Choose an option to toggle: ");
        
        switch (choice) {
            case 1:
                prefs.setShowBookStatus(!prefs.isShowBookStatus());
                System.out.println("Show book status: " + (prefs.isShowBookStatus() ? "Enabled" : "Disabled"));
                break;
            case 2:
                prefs.setShowReturnedLoans(!prefs.isShowReturnedLoans());
                System.out.println("Show returned loans: " + (prefs.isShowReturnedLoans() ? "Enabled" : "Disabled"));
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid option.");
        }
        
        libraryService.saveUserPreferences();
    }
}
