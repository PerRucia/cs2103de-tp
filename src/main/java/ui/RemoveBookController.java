package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import service.LibraryService;

public class RemoveBookController {
    @FXML private TextField isbnField;
    @FXML private Label messageLabel;
    
    private LibraryService libraryService;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
    }
    
    @FXML
    private void handleRemoveBook() {
        String isbn = isbnField.getText().trim();
        
        if (isbn.isEmpty()) {
            messageLabel.setText("Please enter an ISBN");
            return;
        }
        
        try {
            libraryService.removeBook(isbn);
            messageLabel.setText("Book removed successfully!");
            clearFields();
        } catch (Exception e) {
            messageLabel.setText(e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        clearFields();
        messageLabel.setText("");
    }
    
    private void clearFields() {
        isbnField.clear();
    }
} 