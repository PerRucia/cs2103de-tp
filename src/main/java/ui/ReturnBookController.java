package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import service.LibraryService;

public class ReturnBookController {
    @FXML private TextField isbnField;
    @FXML private Label messageLabel;
    
    private LibraryService libraryService;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
    }
    
    @FXML
    private void handleReturnBook() {
        String isbn = isbnField.getText().trim();
        
        if (isbn.isEmpty()) {
            messageLabel.setText("Please enter an ISBN");
            return;
        }
        
        try {
            libraryService.returnBook(isbn);
            messageLabel.setText("Book returned successfully!");
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