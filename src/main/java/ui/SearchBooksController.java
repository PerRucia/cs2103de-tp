package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import service.LibraryService;
import models.*;
import java.util.List;

public class SearchBooksController {
    @FXML private TextField searchField;
    @FXML private ComboBox<SearchCriteria> searchCriteriaComboBox;
    @FXML private ComboBox<SortCriteria> sortCriteriaComboBox;
    @FXML private RadioButton ascendingRadio;
    @FXML private RadioButton descendingRadio;
    @FXML private TableView<Book> resultsTable;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, BookStatus> statusColumn;
    @FXML private TableColumn<Book, String> actionsColumn;
    @FXML private Label messageLabel;
    
    private LibraryService libraryService;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        
        // Get user preferences
        UserPreferences userPrefs = libraryService.getUserPreferences();
        
        // Initialize search criteria combo box
        searchCriteriaComboBox.setItems(FXCollections.observableArrayList(SearchCriteria.values()));
        searchCriteriaComboBox.setValue(userPrefs.getDefaultSearchCriteria());
        
        // Initialize sort criteria combo box
        sortCriteriaComboBox.setItems(FXCollections.observableArrayList(SortCriteria.values()));
        sortCriteriaComboBox.setValue(userPrefs.getDefaultBookSortCriteria());
        
        // Set sort direction based on preferences
        if (userPrefs.isDefaultSortAscending()) {
            ascendingRadio.setSelected(true);
        } else {
            descendingRadio.setSelected(true);
        }
        
        // Initialize table columns
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Set up actions column
        actionsColumn.setCellFactory(column -> {
            return new TableCell<Book, String>() {
                private final Button loanButton = new Button("Loan");
                private final Button removeButton = new Button("Remove");
                
                {
                    // Set the button style
                    loanButton.getStyleClass().add("action-button");
                    removeButton.getStyleClass().add("action-button");

                    // Set up button event handling
                    loanButton.setOnAction(event -> {
                        Book book = getTableView().getItems().get(getIndex());
                        handleLoan(book);
                    });
                    
                    removeButton.setOnAction(event -> {
                        Book book = getTableView().getItems().get(getIndex());
                        handleRemove(book);
                    });
                }
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    
                    if (empty) {
                        setGraphic(null);
                        return;
                    }

                    // Get the books in the current row
                    Book book = getTableView().getItems().get(getIndex());

                    // Set whether the Loan button is available according to the book status
                    boolean isAvailable = book.getStatus() == BookStatus.AVAILABLE;
                    loanButton.setDisable(!isAvailable);

                    // Set the tooltip
                    if (!isAvailable) {
                        Tooltip tooltip = new Tooltip("Book is not available for loan. Current status: " + book.getStatus());
                        Tooltip.install(loanButton, tooltip);
                    } else {
                        loanButton.setTooltip(null);
                    }

                    // Create an HBox and add buttons
                    HBox buttonsBox = new HBox(5);
                    buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);

                    // Add Loan button (displayed to all users)
                    buttonsBox.getChildren().add(loanButton);

                    // If you are an administrator, add a Remove button
                    User currentUser = libraryService.getCurrentUser();
                    if (currentUser != null && currentUser.isAdmin()) {
                        buttonsBox.getChildren().add(removeButton);
                    }
                    
                    setGraphic(buttonsBox);
                }
            };
        });
        
        // Show all books initially
        updateResults(libraryService.getAllBooks());
    }
    
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim();
        SearchCriteria searchCriteria = searchCriteriaComboBox.getValue();
        SortCriteria sortCriteria = sortCriteriaComboBox.getValue();
        boolean ascending = ascendingRadio.isSelected();
        
        if (query.isEmpty()) {
            List<Book> sortedBooks = libraryService.sortBooks(sortCriteria, ascending);
            updateResults(sortedBooks);
            return;
        }
        
        try {
            List<Book> results = libraryService.searchAndSortBooks(query, searchCriteria, sortCriteria, ascending);
            updateResults(results);
            
            if (results.isEmpty()) {
                messageLabel.setText("No books found matching your search criteria.");
            } else {
                messageLabel.setText("Found " + results.size() + " book(s).");
            }
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleClear() {
        searchField.clear();
        searchCriteriaComboBox.setValue(SearchCriteria.ALL);
        sortCriteriaComboBox.setValue(SortCriteria.TITLE);
        ascendingRadio.setSelected(true);
        messageLabel.setText("");
        updateResults(libraryService.getAllBooks());
    }
    
    private void handleLoan(Book book) {
        try {
            // Check the book status, only available books can be borrowed
            if (book.getStatus() != BookStatus.AVAILABLE) {
                messageLabel.setText("Error: Book is not available for loan. Current status: " + book.getStatus());
                return;
            }
            
            libraryService.loanBook(book.getIsbn());
            messageLabel.setText("Book loaned successfully!");

            // Update the status of the current row without refreshing the entire list
            book.setStatus(BookStatus.CHECKED_OUT);
            resultsTable.refresh();
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
    
    private void handleRemove(Book book) {
        // Check if the current user is an administrator
        User currentUser = libraryService.getCurrentUser();
        if (currentUser == null || !currentUser.isAdmin()) {
            messageLabel.setText("Error: Only administrators can remove books");
            return;
        }

        // Create a confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete Book");
        confirmDialog.setContentText("Are you sure you want to delete the book \"" + book.getTitle() + "\" (ISBN: " + book.getIsbn() + ")?");

        // Add confirm and cancel buttons
        ButtonType confirmButton = new ButtonType("Delete");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmDialog.getButtonTypes().setAll(confirmButton, cancelButton);

        // Display the dialog and wait for user response
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                try {
                    libraryService.removeBook(book.getIsbn());
                    messageLabel.setText("Book removed successfully!");

                    // Remove the book from the result table
                    resultsTable.getItems().remove(book);
                } catch (Exception e) {
                    messageLabel.setText("Error: " + e.getMessage());
                }
            }
        });
    }
    
    private void updateResults(List<Book> books) {
        resultsTable.setItems(FXCollections.observableArrayList(books));
    }
} 