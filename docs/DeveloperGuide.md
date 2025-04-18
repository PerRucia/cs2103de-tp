# Library Management System Developer Guide

*(Revised based on feedback)*

## Table of Contents
1. [Introduction](#introduction)
2. [Architecture](#architecture)
3. [Design](#design)
4. [Implementation](#implementation)
5. [Testing](#testing)
6. [Documentation](#documentation)
7. [Development Workflow](#development-workflow)
8. [Dependencies](#dependencies)
9. [Build and Deployment](#build-and-deployment)
10. [Troubleshooting](#troubleshooting)

## Introduction
This guide provides comprehensive information for developers working on the Library Management System. It covers the system's architecture, design decisions, implementation details, and development guidelines.

## Architecture

### System Overview
The Library Management System follows a layered architecture:
- **Presentation Layer**: JavaFX-based UI components (`ui` package). Handles user interaction and displays information.
- **Business Logic Layer**: Core application logic and services (`service` package). Orchestrates operations and enforces rules.
- **Data Access Layer**: Data persistence and retrieval (`storage` package). Manages reading from and writing to files.
- **Domain Layer**: Core business entities and models (`models` package). Represents the data structures like Books, Loans, Users.

### Architectural Diagrams
The following diagrams provide visual representations of the system architecture:

1.  **System Architecture Overview**
    `![System Architecture](diagrams/system_architecture.png)`
    * Shows the layered architecture and component interactions.
    * Illustrates data flow between layers.
    * Highlights external dependencies.

2.  **Class Structure**
    `![Class Structure](diagrams/class_structure.png)`
    * Displays the main classes and their relationships within key packages (`ui`, `service`, `models`, `storage`).
    * Shows inheritance and composition.
    * Highlights key interfaces (if any).

3.  **Book Management Flow**
    `![Book Management Flow](diagrams/book_management.png)`
    * Shows the sequence of operations for book management (e.g., adding, loaning).
    * Illustrates component interactions (e.g., `AddBookController` -> `LibraryService` -> `BookList` -> `GeneralStorage`).
    * Highlights error handling.

4.  **Component Interaction Diagram**
    `![Component Interaction](diagrams/component_interaction.png)`
    * Details the interaction between components during key operations (e.g., User Login, Loan Book).
    * Shows the flow of data and control.
    * Highlights service dependencies.

5.  **Data Flow Diagram**
    `![Data Flow](diagrams/data_flow.png)`
    * Illustrates how data moves through the system (e.g., from UI input to storage).
    * Shows data transformation points.
    * Highlights validation and processing steps.

6.  **Deployment Architecture**
    `![Deployment Architecture](diagrams/deployment_architecture.png)`
    * Shows the runtime environment (JRE + Application JAR).
    * Illustrates configuration requirements (e.g., data file locations).
    * Highlights deployment dependencies (Java version, JavaFX).

7.  **Security Architecture**
    `![Security Architecture](diagrams/security_architecture.png)`
    * Shows authentication (Login screen) and authorization (Admin checks) flows.
    * Illustrates security boundaries (Admin-only features in UI and Service layer).
    * Highlights data protection mechanisms (if any beyond basic file storage).

8.  **Error Handling Flow**
    `![Error Handling Flow](diagrams/error_handling.png)`
    * Shows error detection (e.g., validation in `LibraryService`, file I/O exceptions in `GeneralStorage`) and handling (e.g., displaying messages in UI Controllers).
    * Illustrates user feedback paths (e.g., `messageLabel` updates in controllers).
    * Highlights logging and monitoring points (Standard output or dedicated logging framework if added).

### Key Components
1.  **User Interface (`ui` package)**
    * JavaFX-based GUI.
    * Scene Builder recommended for UI design.
    * FXML (`src/main/resources/fxml`) for layout definition.
    * CSS (`src/main/resources/css`) for styling.
2.  **Core Services (`service.LibraryService`)**
    * Central hub for application logic.
    * Manages interactions between UI, models, and storage.
    * Handles Book Management, Loan Management, User Session, Search/Sort logic.
3.  **Data Management (`storage.GeneralStorage`)**
    * File-based storage system.
    * Uses a **custom CSV-like format** for `bookDatabase.txt` (ISBN,Title,Author,Status).
    * Uses **Java Serialization** (.dat file) for storing `UserPreferences` objects. (Note: Jackson/JSON is mentioned in dependencies but does not appear to be used for primary data storage based on `GeneralStorage.java`).
    * Includes basic data validation logic within services before storage calls.

## Design

### Design Patterns
The system implements several design patterns:
- **MVC (Model-View-Controller)**: Applied loosely with JavaFX. FXML files define the View, Controller classes (`ui` package) handle user input and update the View, and Model classes (`models` package) represent the data. `LibraryService` often acts as an intermediary or part of the Controller/Model logic.
- **Singleton**: `LibraryService` is likely intended to be managed as a single instance throughout the application lifecycle, often provided via a static method in the main `LibraryApp` class, ensuring consistent state management.
- **Factory**: Might be used implicitly or explicitly for creating domain objects (e.g., `Loan` objects within `LoanList` or `LibraryService`).
- **Observer**: JavaFX properties and bindings inherently use the Observer pattern for updating the UI when underlying data changes.
- **Strategy**: The use of `SortCriteria` and `LoanSortCriteria` enums, passed to sorting methods in `LibraryService`, allows changing the sorting algorithm/behavior based on user selection.

### Class Structure
The project source code follows this general package structure:
```
src/main/java/
├── models/         # Data models (Book, Loan, User, UserPreferences, Enums)
├── service/        # Business logic (LibraryService)
├── storage/        # Data persistence (GeneralStorage)
├── ui/             # UI controllers and JavaFX Application classes
│                   # (LoginController, DashboardController, LibraryApp, etc.)
└── utils/          # Utility classes (InputUtil - potentially for text UI)

src/main/resources/
├── fxml/           # FXML layout files for UI screens
├── css/            # CSS stylesheets for UI styling
├── images/         # Application icons or other image assets
└── bookDatabase.txt # Default book data file
```

* **Application Entry Point:** The primary entry point for the JavaFX application is `ui.Launcher`.

## Implementation

### Key Features Implementation
1.  **Book Management**
    * Logic primarily in `service.LibraryService`, `models.BookList`, `models.Book`.
    * ISBN validation occurs within service methods (e.g., checking for non-empty strings).
    * Book status (`BookStatus` enum) is tracked within the `Book` object and updated by `LibraryService` methods.
    * UI handled by `ui.AddBookController`, `ui.RemoveBookController`, `ui.ViewBooksController`, `ui.SearchBooksController`.
2.  **Loan System**
    * Logic primarily in `service.LibraryService`, `models.LoanList`, `models.Loan`.
    * Loan period tracking and due date calculation happen during loan creation in `LibraryService` or `LoanList`. Overdue status checked in `Loan` or `LibraryService`.
    * Return processing handled in `LibraryService`, updating both `Loan` and `Book` status.
    * UI handled by `ui.LoanBookController`, `ui.ReturnBookController`, `ui.MyLoansController`, `ui.ViewLoansController`, `ui.LoanHistoryController`.
3.  **User Management**
    * Authentication handled by `ui.LoginController`, which creates a `models.User` object.
    * Authorization (Admin checks) performed in `service.LibraryService` and UI controllers (e.g., `ui.DashboardController`, `ui.SearchBooksController`) to enable/disable features.
    * User state managed by `LibraryApp` and `LibraryService`.
4.  **Search and Sorting**
    * Logic implemented in `service.LibraryService`, `models.BookList`, `models.LoanList` using `SearchCriteria`, `SortCriteria`, `LoanSortCriteria` enums.
    * Uses Java Streams and Comparators for filtering and sorting collections.
    * UI handled by `ui.SearchBooksController`, `ui.ViewBooksController`, `ui.ViewLoansController`, `ui.MyLoansController`, `ui.LoanHistoryController`.

### Code Style Guidelines
1.  **Naming Conventions**
    * Classes: PascalCase
    * Methods: camelCase
    * Variables: camelCase
    * Constants: UPPER_SNAKE_CASE
2.  **Code Organization**
    * Maximum file length: 500 lines (Guideline)
    * Maximum method length: 50 lines (Guideline)
    * Maximum line length: 100 characters (Guideline)
3.  **Documentation**
    * Javadoc for public methods and classes.
    * Inline comments for complex or non-obvious logic.
    * Clear, descriptive method and variable names.

## Testing

### Testing Strategy
1.  **Unit Tests**
    * JUnit 5 framework
    * Test coverage > 80%
    * Mock objects (Mockito) for dependencies
2.  **Integration Tests**
    * Component interaction testing
    * Data flow verification
    * Error handling validation
3.  **UI Tests**
    * JavaFX Test Framework (TestFX)
    * User interaction simulation
    * Layout verification

### Testing Guidelines
1.  Write tests ideally before or alongside implementing features.
2.  Maintain and review test coverage reports.
3.  Run tests (`./gradlew test`) before committing code.
4.  Document test cases clearly.

## Documentation

### Code Documentation
1.  **Javadoc Requirements**
    * Class descriptions
    * Method documentation (@param, @return, @throws)
    * Parameter descriptions
    * Return value documentation
    * Exception documentation
2.  **Inline Comments**
    * Complex logic explanation
    * Algorithm descriptions
    * Important assumptions or non-obvious decisions

### API Documentation
1.  **Generation:** API documentation (Javadoc) can be generated using the Gradle `javadoc` task (`./gradlew javadoc`).
2.  **Location:** The generated HTML documentation will typically be found in the `build/docs/javadoc` directory after running the task.
3.  **Public API:** Key public APIs include methods in `service.LibraryService`, public getters/setters in `models` classes, and potentially `storage.GeneralStorage` methods.
4.  **Internal API:** Implementation details within service methods, private methods, and UI controller logic are considered internal.

## Development Workflow

### Version Control
1.  **Branch Strategy**
    * main: Production code
    * develop: Development branch
    * feature/*: Feature branches
    * hotfix/*: Emergency fixes
2.  **Commit Guidelines**
    * Atomic commits (one logical change per commit).
    * Descriptive messages (e.g., using conventional commits).
    * Related changes grouped together.

### Code Review Process
1.  Create pull request from feature branch to develop.
2.  Request review from team members.
3.  Address feedback and update the pull request.
4.  Merge after approval.

## Dependencies

### Core Dependencies
- Java 21
- JavaFX 17.0.7 (or compatible)
- JUnit 5 (for testing)
- TestFX (for UI testing)

### Development Tools
- IntelliJ IDEA (or other Java IDE)
- Scene Builder (for FXML editing)
- Git
- Gradle

## Build and Deployment

### Build Process
1.  **Local Build (Compile & Assemble)**
    ```bash
    ./gradlew build
    ```
2.  **Test Execution**
    ```bash
    ./gradlew test
    ```
3.  **Executable JAR Creation (includes dependencies)**
    ```bash
    ./gradlew shadowJar
    ```
    *(The executable JAR will likely be in `build/libs/`)*

### Deployment
1.  **Requirements**
    * Java 21 Runtime Environment (JRE) installed on the target machine.
    * JavaFX runtime components (may need to be installed separately or bundled depending on the JRE distribution and build configuration).
    * Minimum 2GB RAM recommended.
    * ~500MB disk space for application and data files.
2.  **Installation Steps**
    * Copy the executable JAR file (e.g., `cs2103de-tp-all.jar` from `build/libs/`) to the target system.
    * Ensure the data files (`bookDatabase.txt`, `user_preferences.dat`) are placed where the application expects them. By default, this is often the same directory the JAR is run from, or potentially a fixed path like `src/main/resources/` if loaded as resources (though saving back to resources is usually problematic). Clarify data file location strategy if different. The current `LibraryService` uses relative paths which might assume the files are relative to the execution directory.
    * *(Optional)* Create a launch script if needed.
3.  **Configuration**
    * **Data File Paths:** The locations for `bookDatabase.txt` and `user_preferences.dat` are currently hardcoded in `LibraryService.java` and `GeneralStorage.java` as relative paths. For robust deployment, consider making these paths configurable (e.g., via command-line arguments, environment variables, or a separate configuration file).
    * **Initial Data:** Ensure `bookDatabase.txt` exists with the initial book catalog if required for the first run.
    * **Admin Credentials:** Currently, login seems based only on username and the 'admin' checkbox state. There are no configured passwords or admin user lists mentioned. This might need enhancement for a real deployment.

## Troubleshooting

### Common Issues
1.  **Build Problems**
    * **Dependency Conflicts:** Run `./gradlew dependencies` to check the dependency tree. Resolve version clashes.
    * **Version Mismatches:** Ensure correct Java (21) and JavaFX versions are used. Check `JAVA_HOME` and IDE settings.
    * **Resource Access Issues:** Verify paths in code (`src/main/resources/...`) match file locations and build configuration.
2.  **Runtime Errors**
    * **`ClassNotFoundException` / `NoClassDefFoundError`:** Often related to JavaFX modules not being included correctly in the runtime classpath, especially when running outside an IDE. Ensure the `shadowJar` includes JavaFX or that modules are specified correctly when running `java -jar ... --module-path ... --add-modules ...`.
    * **File Access Problems (`IOException` during Load/Save):** Check file permissions in the execution directory. Ensure `bookDatabase.txt` and `user_preferences.dat` are readable/writable. Verify the relative paths resolve correctly based on where the JAR is being run.
    * **JavaFX UI Rendering Issues/Freezes:** Look for long-running tasks blocking the JavaFX Application Thread. Use `Platform.runLater()` for UI updates from background threads if necessary. Check console for JavaFX-specific errors.
    * **`NullPointerException`:** Often occur if `LibraryService` or `currentUser` isn't initialized correctly or if data models are missing expected values (e.g., trying to access `loan.getBook().getTitle()` when `getBook()` returns null).

### Debugging Guidelines
1.  **Logging:** Add more robust logging (e.g., using SLF4j and Logback) instead of just `System.out.println`. Log key events, service method entries/exits, exceptions, and data states.
2.  **Check Data Files:** Manually inspect `bookDatabase.txt` for correct formatting (comma-separated, valid status). Check if `user_preferences.dat` exists and is readable (though its binary format isn't human-readable, its presence/absence/permissions can be checked). Corrupted `.dat` files might cause `ClassNotFoundException` or `IOException` on load; deleting it might reset preferences.
3.  **Verify JavaFX Setup:** Ensure the correct JavaFX SDK is configured in the IDE and included in the runtime environment. Check for missing `--module-path` or `--add-modules` arguments if running modularly.
4.  **Test Edge Cases:** Explicitly test scenarios like empty files, files with invalid formats, loaning/returning non-existent books, administrator actions by regular users, etc..
5.  **Use Debugger:** Step through the code execution in the IDE, especially around UI event handling (`ui` controllers), service logic (`LibraryService`), and file I/O (`GeneralStorage`).

## Support
For development support:
1. Check documentation (User Guide, Developer Guide, Javadoc).
2. Review code examples within the project.
3. Contact senior developers or team leads.
4. Create issue tickets in the project's issue tracker.