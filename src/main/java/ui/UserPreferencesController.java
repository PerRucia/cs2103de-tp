package ui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import service.LibraryService;
import models.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class UserPreferencesController {
    @FXML private ComboBox<SortCriteria> bookSortCriteriaComboBox;
    @FXML private ComboBox<LoanSortCriteria> loanSortCriteriaComboBox;
    @FXML private ComboBox<SearchCriteria> searchCriteriaComboBox;
    @FXML private RadioButton ascendingRadio;
    @FXML private RadioButton descendingRadio;
    @FXML private CheckBox showBookStatusCheckBox;
    @FXML private CheckBox showReturnedLoansCheckBox;
    @FXML private TextField itemsPerPageField;
    
    private LibraryService libraryService;
    private UserPreferences preferences;
    
    @FXML
    public void initialize() {
        libraryService = LibraryApp.getLibraryService();
        preferences = libraryService.getUserPreferences();
        
        // Initialize ComboBoxes
        bookSortCriteriaComboBox.getItems().addAll(SortCriteria.values());
        loanSortCriteriaComboBox.getItems().addAll(LoanSortCriteria.values());
        searchCriteriaComboBox.getItems().addAll(SearchCriteria.values());
        
        // Load current preferences
        loadPreferences();
    }
    
    private void loadPreferences() {
        // Set current values
        bookSortCriteriaComboBox.setValue(preferences.getDefaultBookSortCriteria());
        loanSortCriteriaComboBox.setValue(preferences.getDefaultLoanSortCriteria());
        searchCriteriaComboBox.setValue(preferences.getDefaultSearchCriteria());
        
        if (preferences.isDefaultSortAscending()) {
            ascendingRadio.setSelected(true);
        } else {
            descendingRadio.setSelected(true);
        }
        
        showBookStatusCheckBox.setSelected(preferences.isShowBookStatus());
        showReturnedLoansCheckBox.setSelected(preferences.isShowReturnedLoans());
        itemsPerPageField.setText(String.valueOf(preferences.getItemsPerPage()));
    }
    
    @FXML
    private void handleSave() {
        try {
            // Update preferences
            preferences.setDefaultBookSortCriteria(bookSortCriteriaComboBox.getValue());
            preferences.setDefaultLoanSortCriteria(loanSortCriteriaComboBox.getValue());
            preferences.setDefaultSearchCriteria(searchCriteriaComboBox.getValue());
            preferences.setDefaultSortAscending(ascendingRadio.isSelected());
            
            // Display Settings部分默认值处理 - 因为UI已隐藏，使用默认选择
            preferences.setShowBookStatus(true); // 默认显示书籍状态
            preferences.setShowReturnedLoans(true); // 默认显示已归还的借阅记录
            
            // 设置默认每页项目数
            preferences.setItemsPerPage(10); // 使用10作为默认值
            
            // Save preferences
            libraryService.saveUserPreferences();
            
            showMessage("Preferences saved successfully!");
            
            // Go back to dashboard
            goToDashboard();
        } catch (Exception e) {
            showError("Error saving preferences: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleReset() {
        preferences = new UserPreferences(); // Create new preferences with default values
        
        // 只加载可见组件的值
        bookSortCriteriaComboBox.setValue(preferences.getDefaultBookSortCriteria());
        loanSortCriteriaComboBox.setValue(preferences.getDefaultLoanSortCriteria());
        searchCriteriaComboBox.setValue(preferences.getDefaultSearchCriteria());
        
        if (preferences.isDefaultSortAscending()) {
            ascendingRadio.setSelected(true);
        } else {
            descendingRadio.setSelected(true);
        }
        
        // Display Settings已被隐藏，不需要更新其UI值
        
        showMessage("Preferences reset to default values");
    }
    
    private void goToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
            Parent dashboard = loader.load();
            
            // Get the current scene from any control in the view
            bookSortCriteriaComboBox.getScene().setRoot(dashboard);
        } catch (IOException e) {
            showError("Error navigating to dashboard: " + e.getMessage());
        }
    }
    
    private void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 