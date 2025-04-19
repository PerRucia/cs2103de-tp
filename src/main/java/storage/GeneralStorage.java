package storage;

import models.BookList;
import models.Book;
import models.BookStatus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import models.UserPreferences;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;

public class GeneralStorage {
    /**
     * Loads the book list from the database.
     * This method reads the book data from a file and populates the BookList object.
     */
    public static BookList loadBookList(String filename) {
        File file = new File(filename);
        
        if (!file.exists()) {
            return null;
        }
        
        BookList bookList = new BookList();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Book book = new Book(parts[0], parts[1], parts[2]);
                    book.setStatus(BookStatus.valueOf(parts[3]));
                    bookList.addBook(book);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading book list: " + e.getMessage());
        }
        return bookList;
    }

    /**
     * Loads the book list from an InputStream.
     * This method reads the book data from the InputStream and populates the BookList object.
     * @param inputStream The InputStream to read the book data from.
     * @return A BookList object containing the loaded books, or null if the InputStream is null.
     */
    public static BookList loadBookList(InputStream inputStream) {
        if (inputStream == null) {
            System.out.println("InputStream is null. Cannot load book list.");
            return null;
        }

        BookList bookList = new BookList();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Book book = new Book(parts[0], parts[1], parts[2]);
                    book.setStatus(BookStatus.valueOf(parts[3]));
                    bookList.addBook(book);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading book list from InputStream: " + e.getMessage());
        }

        return bookList;
    }

    /**
     * Saves the book list to the database.
     * This method writes the book data to a file.
     * @param bookList The BookList object containing the books to be saved.
     */
    public static void saveBookList(String filename, BookList bookList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<String, Book> entry : bookList.getBooks().entrySet()) {
                Book book = entry.getValue();
                writer.write(String.format("%s,%s,%s,%s%n",
                        book.getIsbn(),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getStatus()));
            }
        } catch (IOException e) {
            System.out.println("Error saving book list: " + e.getMessage());
        }
    }

    /**
     * Save user preferences
     * @param filename File path
     * @param preferences User preferences object
     */
    public static void saveUserPreferences(String filename, UserPreferences preferences) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(preferences);
        } catch (IOException e) {
            System.out.println("Error saving user preferences: " + e.getMessage());
        }
    }

    /**
     * Load user preferences
     * @param filename File path
     * @return User preferences object, returns default preferences if loading fails
     */
    public static UserPreferences loadUserPreferences(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return new UserPreferences();
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (UserPreferences) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading user preferences: " + e.getMessage());
            return new UserPreferences();
        }
    }
}
