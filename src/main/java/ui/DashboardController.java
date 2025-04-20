package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import service.LibraryService;
import models.User;

public class DashboardController {
    @FXML private StackPane contentPane;
    @FXML private VBox sidebar;
    
    private LibraryService libraryService;
    private User currentUser;

    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        currentUser = libraryService.getCurrentUser();

        // Set dashboard window size (add these lines)
        javafx.application.Platform.runLater(() -> {
            javafx.stage.Stage stage = (javafx.stage.Stage) contentPane.getScene().getWindow();
            stage.setWidth(1280);  // Set your preferred width
            stage.setHeight(720);  // Set your preferred height
            stage.centerOnScreen();
        });
        
        // Hide admin-only buttons if not admin
        if (!currentUser.isAdmin()) {
            sidebar.getChildren().forEach(node -> {
                if (node instanceof Button && node.getStyleClass().contains("admin-only")) {
                    node.setVisible(false);
                    node.setManaged(false);
                }
            });
        }
    }

    @FXML
    private void handleViewAllBooks() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewBooks.fxml"));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLoanBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoanBook.fxml"));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturnBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReturnBook.fxml"));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AddBook.fxml"));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRemoveBook() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RemoveBook.fxml"));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewLoans() {
        // Implementation for viewing loans
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ViewLoans.fxml"));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSearchBooks() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SearchBooks.fxml"));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Implementation for logout
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            // Get the current stage
            javafx.stage.Stage stage = (javafx.stage.Stage) contentPane.getScene().getWindow();
            
            // Set the login screen as the root
            contentPane.getScene().setRoot(root);
            
            // Reset window size to match the login dimensions
            stage.setWidth(450);  // Same as in LibraryApp
            stage.setHeight(500); // Same as in LibraryApp
            stage.centerOnScreen();
            stage.setResizable(false);           
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 