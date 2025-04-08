package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import service.LibraryService;
import models.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class MyLoansController {
    @FXML private TableView<Loan> loansTable;
    @FXML private TableColumn<Loan, String> isbnColumn;
    @FXML private TableColumn<Loan, String> titleColumn;
    @FXML private TableColumn<Loan, String> authorColumn;
    @FXML private TableColumn<Loan, LocalDate> loanDateColumn;
    @FXML private TableColumn<Loan, LocalDate> dueDateColumn;
    @FXML private TableColumn<Loan, String> statusColumn;
    @FXML private TableColumn<Loan, Void> actionsColumn;
    @FXML private Label messageLabel;
    
    private LibraryService libraryService;
    private User currentUser;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        currentUser = libraryService.getCurrentUser();
        
        // Set up table columns
        isbnColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook().getIsbn()));
        titleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook().getTitle()));
        authorColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook().getAuthor()));
        loanDateColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getLoanDate()));
        dueDateColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getDueDate()));
        
        // Status column shows if book is overdue, currently loaned, or returned
        statusColumn.setCellValueFactory(cellData -> {
            Loan loan = cellData.getValue();
            String status;
            
            if (loan.getReturnDate() != null) {
                status = "Returned on " + loan.getReturnDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            } else if (loan.getDueDate().isBefore(LocalDate.now())) {
                status = "OVERDUE";
            } else {
                status = "Borrowed";
            }
            
            return new SimpleStringProperty(status);
        });
        
        // Set up actions column with Return button
        setupActionsColumn();
        
        // Load loans with user preferences
        refreshMyLoans();
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
    
    private void refreshMyLoans() {
        try {
            // 获取用户偏好设置
            UserPreferences userPrefs = libraryService.getUserPreferences();
            
            // 使用新方法直接获取当前用户的借阅记录，根据偏好决定是否包含已归还的书籍
            List<Loan> myLoans = libraryService.getMyLoans(userPrefs.isShowReturnedLoans());
            
            // 按照用户偏好进行排序
            LoanSortCriteria sortCriteria = userPrefs.getDefaultLoanSortCriteria();
            boolean ascending = userPrefs.isDefaultSortAscending();
            sortLoans(myLoans, sortCriteria, ascending);
            
            // 更新表格
            loansTable.setItems(FXCollections.observableArrayList(myLoans));
            
            // 更新消息
            messageLabel.setText("Found " + myLoans.size() + " loan(s)");
        } catch (Exception e) {
            messageLabel.setText("Error loading loans: " + e.getMessage());
        }
    }
    
    private void sortLoans(List<Loan> loans, LoanSortCriteria criteria, boolean ascending) {
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
        
        loans.sort(comparator);
    }
    
    @FXML
    private void handleSort() {
        // Create a dialog for sort options
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sort My Loans");
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

        // Add show returned loans option
        CheckBox showReturnedLoansCheck = new CheckBox("Show returned loans");
        showReturnedLoansCheck.setSelected(userPrefs.isShowReturnedLoans());

        // Add controls to dialog
        dialog.getDialogPane().setContent(new VBox(10,
            new Label("Sort by:"),
            criteriaChoice,
            new Label("Direction:"),
            directionChoice,
            showReturnedLoansCheck
        ));

        // Add buttons
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Show dialog and handle result
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                LoanSortCriteria criteria = criteriaChoice.getValue();
                boolean ascending = directionChoice.getValue().equals("Ascending");
                boolean showReturned = showReturnedLoansCheck.isSelected();
                
                // Save preferences
                userPrefs.setDefaultLoanSortCriteria(criteria);
                userPrefs.setDefaultSortAscending(ascending);
                userPrefs.setShowReturnedLoans(showReturned);
                libraryService.saveUserPreferences();
                
                // Refresh with new preferences
                refreshMyLoans();
            }
        });
    }
    
    @FXML
    private void handleRefresh() {
        refreshMyLoans();
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent dashboard = loader.load();
            loansTable.getScene().setRoot(dashboard);
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Error: " + e.getMessage());
        }
    }
} 