package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import service.LibraryService;
import models.Loan;
import models.LoanSortCriteria;
import models.UserPreferences;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ViewLoansController {
    @FXML private TableView<Loan> loansTable;
    @FXML private TableColumn<Loan, String> isbnColumn;
    @FXML private TableColumn<Loan, String> titleColumn;
    @FXML private TableColumn<Loan, String> userIdColumn;
    @FXML private TableColumn<Loan, LocalDate> loanDateColumn;
    @FXML private TableColumn<Loan, LocalDate> dueDateColumn;
    @FXML private TableColumn<Loan, String> statusColumn;
    @FXML private TableColumn<Loan, Void> actionsColumn;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchTypeComboBox;
    @FXML private Label messageLabel;
    @FXML private Label recordCountLabel;
    
    private LibraryService libraryService;
    private List<Loan> allLoans;
    private List<Loan> filteredLoans;
    
    // Search types for the combo box
    private final String[] SEARCH_TYPES = {
        "All Fields", "User ID", "Book Title", "Book ISBN", "Status"
    };
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        
        // Set up search type combo box
        searchTypeComboBox.setItems(FXCollections.observableArrayList(SEARCH_TYPES));
        searchTypeComboBox.setValue(SEARCH_TYPES[0]); // Default to "All Fields"
        
        // Set up table columns
        isbnColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook().getIsbn()));
        titleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook().getTitle()));
        userIdColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBorrower().getId()));
        loanDateColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getLoanDate()));
        dueDateColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getDueDate()));
            
        // Status column shows if book is overdue, currently loaned, or returned
        statusColumn.setCellValueFactory(cellData -> {
            Loan loan = cellData.getValue();
            String status;
            
            if (loan.getReturnDate() != null) {
                status = "Returned";
            } else if (loan.getDueDate().isBefore(LocalDate.now())) {
                status = "OVERDUE";
            } else {
                status = "Borrowed";
            }
            
            return new SimpleStringProperty(status);
        });
        
        // Set up actions column with Return button
        setupActionsColumn();
        
        try {
            // Load loans with user preferences
            loadLoans();
        } catch (Exception e) {
            messageLabel.setText("Error loading loans: " + e.getMessage());
        }
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> {
            return new TableCell<>() {
                private final Button returnButton = new Button("Return");
                
                {
                    returnButton.getStyleClass().add("action-button");
                    returnButton.setOnAction(event -> {
                        Loan loan = getTableView().getItems().get(getIndex());
                        handleReturnBook(loan);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Loan loan = getTableView().getItems().get(getIndex());
                        // Only show return button if the book has not been returned yet
                        if (loan.getReturnDate() == null) {
                            setGraphic(returnButton);
                        } else {
                            setGraphic(null);
                        }
                    }
                }
            };
        });
    }
    
    private void handleReturnBook(Loan loan) {
        // Create confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Return Book");
        confirmDialog.setHeaderText("Confirm Return");
        confirmDialog.setContentText("Do you want to return the book: \"" + loan.getBook().getTitle() + "\"?");
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Return the book
                    libraryService.returnBook(loan.getBook().getIsbn());
                    
                    // Update the loan object
                    loan.returnBook();
                    
                    // Refresh the table
                    loansTable.refresh();
                    
                    // Show success message
                    messageLabel.setText("Book returned successfully: " + loan.getBook().getTitle());
                } catch (Exception e) {
                    messageLabel.setText("Error returning book: " + e.getMessage());
                }
            }
        });
    }
    
    private void loadLoans() {
        // Load all current loans
        allLoans = new ArrayList<>(libraryService.viewLoans());
        filteredLoans = new ArrayList<>(allLoans);
        
        // Update the table
        updateTable();
    }
    
    private void refreshLoans() {
        loadLoans();
        messageLabel.setText("");
    }
    
    private void updateTable() {
        loansTable.setItems(FXCollections.observableArrayList(filteredLoans));
        recordCountLabel.setText(String.valueOf(filteredLoans.size()));
    }
    
    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        String searchType = searchTypeComboBox.getValue();
        
        if (query.isEmpty()) {
            filteredLoans = new ArrayList<>(allLoans);
            updateTable();
            return;
        }
        
        // Filter loans based on search criteria
        filteredLoans = allLoans.stream().filter(loan -> {
            switch (searchType) {
                case "User ID":
                    return loan.getBorrower().getId().toLowerCase().contains(query);
                case "Book Title":
                    return loan.getBook().getTitle().toLowerCase().contains(query);
                case "Book ISBN":
                    return loan.getBook().getIsbn().toLowerCase().contains(query);
                case "Status":
                    String status = getStatusString(loan).toLowerCase();
                    return status.contains(query);
                case "All Fields":
                default:
                    return loan.getBorrower().getId().toLowerCase().contains(query) ||
                           loan.getBook().getTitle().toLowerCase().contains(query) ||
                           loan.getBook().getIsbn().toLowerCase().contains(query) ||
                           loan.getBook().getAuthor().toLowerCase().contains(query) ||
                           getStatusString(loan).toLowerCase().contains(query);
            }
        }).collect(Collectors.toList());
        
        updateTable();
        
        if (filteredLoans.isEmpty()) {
            messageLabel.setText("No loans found matching your search criteria.");
        } else {
            messageLabel.setText("Found " + filteredLoans.size() + " loan(s).");
        }
    }
    
    private String getStatusString(Loan loan) {
        if (loan.getReturnDate() != null) {
            return "Returned";
        } else if (loan.getDueDate().isBefore(LocalDate.now())) {
            return "OVERDUE";
        } else {
            return "Borrowed";
        }
    }
    
    @FXML
    private void handleReset() {
        searchField.clear();
        searchTypeComboBox.setValue(SEARCH_TYPES[0]);
        filteredLoans = new ArrayList<>(allLoans);
        updateTable();
        messageLabel.setText("");
    }
    
    @FXML
    private void handleSort() {
        // Create a dialog for sort options
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sort Loans");
        dialog.setHeaderText("Choose sorting criteria and direction");

        // Get user preferences
        UserPreferences userPrefs = libraryService.getUserPreferences();

        // Create sort criteria choice box
        ChoiceBox<LoanSortCriteria> criteriaChoice = new ChoiceBox<>();
        criteriaChoice.getItems().addAll(LoanSortCriteria.values());
        criteriaChoice.setValue(userPrefs.getDefaultLoanSortCriteria());

        // Create sort direction choice box
        ChoiceBox<String> directionChoice = new ChoiceBox<>();
        directionChoice.getItems().addAll("Ascending", "Descending");
        directionChoice.setValue(userPrefs.isDefaultSortAscending() ? "Ascending" : "Descending");

        // Add controls to dialog
        dialog.getDialogPane().setContent(new VBox(10,
            new Label("Sort by:"),
            criteriaChoice,
            new Label("Direction:"),
            directionChoice
        ));

        // Add buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LoanSortCriteria criteria = criteriaChoice.getValue();
                boolean ascending = directionChoice.getValue().equals("Ascending");
                sortLoans(criteria, ascending);
                updateTable();
            }
        });
    }
    
    private void sortLoans(LoanSortCriteria criteria, boolean ascending) {
        Comparator<Loan> comparator;
        switch (criteria) {
            case LOAN_DATE:
                comparator = Comparator.comparing(Loan::getLoanDate);
                break;
            case DUE_DATE:
                comparator = Comparator.comparing(Loan::getDueDate);
                break;
            case RETURN_DATE:
                comparator = (loan1, loan2) -> {
                    if (loan1.getReturnDate() == null && loan2.getReturnDate() == null) {
                        return 0;
                    } else if (loan1.getReturnDate() == null) {
                        return 1;
                    } else if (loan2.getReturnDate() == null) {
                        return -1;
                    } else {
                        return loan1.getReturnDate().compareTo(loan2.getReturnDate());
                    }
                };
                break;
            case BOOK_TITLE:
                comparator = Comparator.comparing(loan -> loan.getBook().getTitle());
                break;
            case BOOK_AUTHOR:
                comparator = Comparator.comparing(loan -> loan.getBook().getAuthor());
                break;
            case BOOK_ISBN:
                comparator = Comparator.comparing(loan -> loan.getBook().getIsbn());
                break;
            case STATUS:
                comparator = (loan1, loan2) -> {
                    boolean isOverdue1 = loan1.getReturnDate() == null && 
                                         loan1.getDueDate().isBefore(LocalDate.now());
                    boolean isOverdue2 = loan2.getReturnDate() == null && 
                                         loan2.getDueDate().isBefore(LocalDate.now());
                    boolean isReturned1 = loan1.getReturnDate() != null;
                    boolean isReturned2 = loan2.getReturnDate() != null;
                    
                    if (isOverdue1 && !isOverdue2) return 1;
                    if (!isOverdue1 && isOverdue2) return -1;
                    if (isReturned1 && !isReturned2) return -1;
                    if (!isReturned1 && isReturned2) return 1;
                    return 0;
                };
                break;
            default:
                comparator = Comparator.comparing(Loan::getLoanDate);
        }
        
        if (!ascending) {
            comparator = comparator.reversed();
        }
        
        filteredLoans.sort(comparator);
    }
    
    @FXML
    private void handleRefresh() {
        refreshLoans();
    }
} 