package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
// import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import service.LibraryService;
import models.User;
import java.io.IOException;

public class DashboardController {
    @FXML private StackPane contentPane;
    @FXML private VBox sidebar;
    
    private LibraryService libraryService;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        
        // 根据用户角色隐藏/显示菜单项
        User currentUser = libraryService.getCurrentUser();
        if (currentUser != null) {
            // 获取所有菜单按钮
            sidebar.getChildren().forEach(node -> {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    
                    // 处理管理员专属菜单项
                    if (button.getStyleClass().contains("admin-only") && !currentUser.isAdmin()) {
                        button.setVisible(false);
                        button.setManaged(false);
                    }
                    
                    // 处理普通用户专属菜单项
                    if (button.getStyleClass().contains("user-only") && currentUser.isAdmin()) {
                        button.setVisible(false);
                        button.setManaged(false);
                    }
                }
            });
        }
    }

    // 加载指定的FXML内容到contentPane
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();
            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewAllBooks() {
        loadContent("/fxml/ViewBooks.fxml");
    }

    @FXML
    private void handleSearchBooks() {
        loadContent("/fxml/SearchBooks.fxml");
    }

    @FXML
    private void handleLoanBook() {
        loadContent("/fxml/LoanBook.fxml");
    }

    @FXML
    private void handleReturnBook() {
        loadContent("/fxml/ReturnBook.fxml");
    }

    @FXML
    private void handleMyLoans() {
        loadContent("/fxml/MyLoans.fxml");
    }

    @FXML
    private void handleAddBook() {
        loadContent("/fxml/AddBook.fxml");
    }

    @FXML
    private void handleViewLoans() {
        loadContent("/fxml/ViewLoans.fxml");
    }

    @FXML
    private void handleLoanHistory() {
        loadContent("/fxml/LoanHistory.fxml");
    }

    @FXML
    private void handleUserPreferences() {
        loadContent("/fxml/UserPreferences.fxml");
    }

    @FXML
    private void handleLogout() {
        try {
            libraryService.logout();
            
            // 返回登录页面
            Parent loginScreen = FXMLLoader.load(getClass().getResource("/fxml/Login.fxml"));
            contentPane.getScene().setRoot(loginScreen);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // private void showError(String message) {
    //     Alert alert = new Alert(Alert.AlertType.ERROR);
    //     alert.setTitle("Error");
    //     alert.setHeaderText(null);
    //     alert.setContentText(message);
    //     alert.showAndWait();
    // }
} 