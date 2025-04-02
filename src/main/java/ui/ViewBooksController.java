package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import models.Book;
import service.LibraryService;
import models.SortCriteria;

import java.util.List;

public class ViewBooksController {
    @FXML private TableView<Book> booksTable;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> statusColumn;
    
    private LibraryService libraryService;
    private ObservableList<Book> booksList;

    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        
        // Set up table columns
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Load initial data
        refreshBooks();
    }

    private void refreshBooks() {
        List<Book> books = libraryService.getAllBooks();
        booksList = FXCollections.observableArrayList(books);
        booksTable.setItems(booksList);
    }

    @FXML
    private void handleSort() {
        // Create a dialog for sort options
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sort Books");
        dialog.setHeaderText("Choose sorting criteria and direction");

        // Create sort criteria choice box
        ChoiceBox<SortCriteria> criteriaChoice = new ChoiceBox<>();
        criteriaChoice.getItems().addAll(SortCriteria.values());
        criteriaChoice.setValue(SortCriteria.TITLE);

        // Create sort direction choice box
        ChoiceBox<String> directionChoice = new ChoiceBox<>();
        directionChoice.getItems().addAll("Ascending", "Descending");
        directionChoice.setValue("Ascending");

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
                SortCriteria criteria = criteriaChoice.getValue();
                boolean ascending = directionChoice.getValue().equals("Ascending");
                libraryService.sortBooks(criteria, ascending);
                refreshBooks();
            }
        });
    }

    @FXML
    private void handleRefresh() {
        refreshBooks();
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent dashboard = loader.load();
            booksTable.getScene().setRoot(dashboard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 