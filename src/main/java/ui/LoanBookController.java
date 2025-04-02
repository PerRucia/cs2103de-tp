package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import service.LibraryService;

public class LoanBookController {
    @FXML private TextField isbnField;
    @FXML private Label messageLabel;
    @FXML private Label userLabel;
    
    private LibraryService libraryService;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        
        // Show current user information
        if (libraryService.getCurrentUser() != null) {
            userLabel.setText("Logged in as: " + libraryService.getCurrentUser().getId());
        }
    }
    
    @FXML
    private void handleLoanBook() {
        String isbn = isbnField.getText().trim();
        
        if (isbn.isEmpty()) {
            messageLabel.setText("Please enter an ISBN");
            return;
        }
        
        try {
            libraryService.loanBook(isbn);
            messageLabel.setText("Book loaned successfully!");
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