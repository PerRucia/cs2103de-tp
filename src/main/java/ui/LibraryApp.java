package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import service.LibraryService;
import models.User;

public class LibraryApp extends Application {
    private static LibraryService libraryService;
    private static User currentUser;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        initialize();
        
        // Load the login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        // Set application icon
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/library_icon.png")));
        
        stage.setTitle("Library Management System");
        stage.setScene(scene);
        stage.show();
    }

    public static LibraryService getLibraryService() {
        if (libraryService == null) {
            initialize();
        }
        return libraryService;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        if (libraryService != null) {
            libraryService.setCurrentUser(user);
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void initialize() {
        if (libraryService == null) {
            libraryService = new LibraryService();
        }
    }

    @Override
    public void stop() {
        if (libraryService != null) {
            libraryService.saveData();
        }
    }
} 