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
import models.UserPreferences;

import java.util.List;
import java.util.Comparator;

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
        
        // 设置列头排序功能
        setupColumnSorting();
        
        // Load initial data with user preferences
        refreshBooksWithPreferences();
    }

    /**
     * 设置表格列的排序功能
     */
    private void setupColumnSorting() {
        // 禁用所有列的排序功能
        isbnColumn.setSortable(false);
        titleColumn.setSortable(false);
        authorColumn.setSortable(false);
        statusColumn.setSortable(false);
        
        // 清除任何排序标记
        booksTable.getSortOrder().clear();
    }

    private void refreshBooks() {
        List<Book> books = libraryService.getAllBooks();
        booksList = FXCollections.observableArrayList(books);
        booksTable.setItems(booksList);
    }
    
    private void refreshBooksWithPreferences() {
        // Get user preferences
        UserPreferences userPrefs = libraryService.getUserPreferences();
        
        // Apply sort preferences
        SortCriteria sortCriteria = userPrefs.getDefaultBookSortCriteria();
        boolean ascending = userPrefs.isDefaultSortAscending();
        
        // Get sorted books according to preferences
        List<Book> sortedBooks = libraryService.sortBooks(sortCriteria, ascending);
        booksList = FXCollections.observableArrayList(sortedBooks);
        booksTable.setItems(booksList);
    }

    @FXML
    private void handleSort() {
        // Create a dialog for sort options
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Sort Books");
        dialog.setHeaderText("Choose sorting criteria and direction");

        // Get user preferences
        UserPreferences userPrefs = libraryService.getUserPreferences();

        // Create sort criteria choice box
        ChoiceBox<SortCriteria> criteriaChoice = new ChoiceBox<>();
        criteriaChoice.getItems().addAll(SortCriteria.values());
        criteriaChoice.setValue(userPrefs.getDefaultBookSortCriteria());

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
                SortCriteria criteria = criteriaChoice.getValue();
                boolean ascending = directionChoice.getValue().equals("Ascending");
                List<Book> sortedBooks = libraryService.sortBooks(criteria, ascending);
                booksList = FXCollections.observableArrayList(sortedBooks);
                booksTable.setItems(booksList);
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