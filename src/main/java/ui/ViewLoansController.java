package ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import service.LibraryService;
import models.Loan;
import java.time.LocalDate;
import java.util.ArrayList;

public class ViewLoansController {
    @FXML private TableView<Loan> loansTable;
    @FXML private TableColumn<Loan, String> isbnColumn;
    @FXML private TableColumn<Loan, String> titleColumn;
    @FXML private TableColumn<Loan, String> userIdColumn;
    @FXML private TableColumn<Loan, LocalDate> loanDateColumn;
    @FXML private Label messageLabel;
    
    private LibraryService libraryService;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        
        isbnColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook().getIsbn()));
        titleColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBook().getTitle()));
        userIdColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getBorrower().getId()));
        loanDateColumn.setCellValueFactory(cellData -> 
            new SimpleObjectProperty<>(cellData.getValue().getLoanDate()));
        
        try {
            loansTable.setItems(FXCollections.observableArrayList(libraryService.viewLoans()));
        } catch (Exception e) {
            messageLabel.setText("Error loading loans: " + e.getMessage());
        }
    }
} 