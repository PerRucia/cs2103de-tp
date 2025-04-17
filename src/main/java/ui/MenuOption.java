package ui;

public enum MenuOption {
    VIEW_ALL_BOOKS(1, "View all books", false),
    LOAN_BOOK(2, "Loan a book", false),
    RETURN_BOOK(3, "Return a book", false),
    ADD_BOOK(4, "Add a book", true),
    REMOVE_BOOK(5, "Remove a book", true),
    VIEW_LOANS(10, "View Loans", true),
    EXIT(7, "Exit", false),
    VIEW_SORTED_BOOKS(8, "View sorted books", false),
    VIEW_SORTED_LOANS(9, "View sorted loans", true),
    SEARCH_BOOKS(10, "Search books", false),
    LOGOUT(11, "Logout", false);

    private final int choice;
    private final String description;
    private final boolean adminOnly;

    MenuOption(int choice, String description, boolean adminOnly) {
        this.choice = choice;
        this.description = description;
        this.adminOnly = adminOnly;
    }

    public int getChoice() {
        return choice;
    }

    public String getDescription() {
        return description;
    }

    public boolean isAdminOnly() {
        return adminOnly;
    }

    public static MenuOption fromChoice(int choice, boolean isAdmin) {
        for (MenuOption option : values()) {
            if (option.choice == choice) {
                if (option.adminOnly && !isAdmin) {
                    throw new IllegalArgumentException("This option is only available for administrators.");
                }
                return option;
            }
        }
        throw new IllegalArgumentException("Invalid menu choice.");
    }
} 