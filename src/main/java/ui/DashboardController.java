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
    private void handleUserPreferences() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/UserPreferences.fxml"));
            Parent view = loader.load();
            contentPane.getChildren().setAll(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            
            contentPane.getScene().setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 