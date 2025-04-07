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
        
        // Initialize search criteria combo box
        searchCriteriaComboBox.setItems(FXCollections.observableArrayList(SearchCriteria.values()));
        searchCriteriaComboBox.setValue(SearchCriteria.ALL);
        
        // Initialize sort criteria combo box
        sortCriteriaComboBox.setItems(FXCollections.observableArrayList(SortCriteria.values()));
        sortCriteriaComboBox.setValue(SortCriteria.TITLE);
        
        // Initialize table columns
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Set up actions column
        actionsColumn.setCellFactory(column -> {
            TableCell<Book, String> cell = new TableCell<>() {
                private final Button loanButton = new Button("Loan");
                private final Button removeButton = new Button("Remove");
                private final HBox buttons = new HBox(5, loanButton, removeButton);
                
                {
                    buttons.setAlignment(javafx.geometry.Pos.CENTER);
                    loanButton.getStyleClass().add("action-button");
                    removeButton.getStyleClass().add("action-button");
                    removeButton.getStyleClass().add("admin-only");
                    
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
                    } else {
                        setGraphic(buttons);
                    }
                }
            };
            return cell;
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
            List<Book> allBooks = libraryService.getAllBooks();
            libraryService.sortBooks(sortCriteria, ascending);
            updateResults(allBooks);
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
            libraryService.loanBook(book.getIsbn());
            messageLabel.setText("Book loaned successfully!");
            updateResults(libraryService.getAllBooks());
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
    
    private void handleRemove(Book book) {
        try {
            libraryService.removeBook(book.getIsbn());
            messageLabel.setText("Book removed successfully!");
            updateResults(libraryService.getAllBooks());
        } catch (Exception e) {
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
    
    private void updateResults(List<Book> books) {
        resultsTable.setItems(FXCollections.observableArrayList(books));
    }
} 