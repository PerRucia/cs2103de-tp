package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import service.LibraryService;

public class AddBookController {
    @FXML private TextField isbnField;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private Label messageLabel;
    
    private LibraryService libraryService;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
    }
    
    @FXML
    private void handleAddBook() {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        
        if (isbn.isEmpty() || title.isEmpty() || author.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }
        
        try {
            libraryService.addBook(isbn, title, author);
            messageLabel.setText("Book added successfully!");
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
        titleField.clear();
        authorField.clear();
    }
} 