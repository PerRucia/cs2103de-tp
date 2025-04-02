package models;

import java.io.Serializable;

/**
 * Stores user preferences for the library system.
 */
public class UserPreferences implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Default sort preferences
    private SortCriteria defaultBookSortCriteria = SortCriteria.TITLE;
    private LoanSortCriteria defaultLoanSortCriteria = LoanSortCriteria.LOAN_DATE;
    private SearchCriteria defaultSearchCriteria = SearchCriteria.ALL_FIELDS;
    private boolean defaultSortAscending = true;
    
    // Display preferences
    private int itemsPerPage = 10;
    private boolean showBookStatus = true;
    private boolean showReturnedLoans = true;
    
    /**
     * Constructs a new UserPreferences object with default values.
     */
    public UserPreferences() {
        // Default constructor
    }
    
    /**
     * Gets the default book sort criteria.
     * @return The default book sort criteria.
     */
    public SortCriteria getDefaultBookSortCriteria() {
        return defaultBookSortCriteria;
    }
    
    /**
     * Sets the default book sort criteria.
     * @param criteria The default book sort criteria to set.
     */
    public void setDefaultBookSortCriteria(SortCriteria criteria) {
        this.defaultBookSortCriteria = criteria;
    }
    
    /**
     * Gets the default loan sort criteria.
     * @return The default loan sort criteria.
     */
    public LoanSortCriteria getDefaultLoanSortCriteria() {
        return defaultLoanSortCriteria;
    }
    
    /**
     * Sets the default loan sort criteria.
     * @param criteria The default loan sort criteria to set.
     */
    public void setDefaultLoanSortCriteria(LoanSortCriteria criteria) {
        this.defaultLoanSortCriteria = criteria;
    }
    
    /**
     * Gets the default search criteria.
     * @return The default search criteria.
     */
    public SearchCriteria getDefaultSearchCriteria() {
        return defaultSearchCriteria;
    }
    
    /**
     * Sets the default search criteria.
     * @param criteria The default search criteria to set.
     */
    public void setDefaultSearchCriteria(SearchCriteria criteria) {
        this.defaultSearchCriteria = criteria;
    }
    
    /**
     * Checks if the default sort direction is ascending.
     * @return true if the default sort direction is ascending, false otherwise.
     */
    public boolean isDefaultSortAscending() {
        return defaultSortAscending;
    }
    
    /**
     * Sets the default sort direction.
     * @param ascending true for ascending, false for descending.
     */
    public void setDefaultSortAscending(boolean ascending) {
        this.defaultSortAscending = ascending;
    }
    
    /**
     * Gets the number of items to display per page.
     * @return The number of items per page.
     */
    public int getItemsPerPage() {
        return itemsPerPage;
    }
    
    /**
     * Sets the number of items to display per page.
     * @param itemsPerPage The number of items per page to set.
     */
    public void setItemsPerPage(int itemsPerPage) {
        if (itemsPerPage > 0) {
            this.itemsPerPage = itemsPerPage;
        }
    }
    
    /**
     * Checks if book status should be displayed.
     * @return true if book status should be displayed, false otherwise.
     */
    public boolean isShowBookStatus() {
        return showBookStatus;
    }
    
    /**
     * Sets whether book status should be displayed.
     * @param showBookStatus true to show book status, false to hide it.
     */
    public void setShowBookStatus(boolean showBookStatus) {
        this.showBookStatus = showBookStatus;
    }
    
    /**
     * Checks if returned loans should be displayed.
     * @return true if returned loans should be displayed, false otherwise.
     */
    public boolean isShowReturnedLoans() {
        return showReturnedLoans;
    }
    
    /**
     * Sets whether returned loans should be displayed.
     * @param showReturnedLoans true to show returned loans, false to hide it.
     */
    public void setShowReturnedLoans(boolean showReturnedLoans) {
        this.showReturnedLoans = showReturnedLoans;
    }
}