package storage;

import models.BookList;
import models.Book;
import models.BookStatus;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import models.UserPreferences;


public class GeneralStorage {
    /**
     * Loads the book list from the database.
     * This method reads the book data from a file and populates the BookList object.
     */
    public static BookList loadBookList(String filePath) {
        BookList bookList = new BookList();
        // Load books from the database with buffered reader
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] bookData = line.split(",");
                if (bookData.length == 4) {
                    String isbn = bookData[0];
                    String title = bookData[1];
                    String author = bookData[2];
                    BookStatus status = BookStatus.valueOf(bookData[3]);
                    Book book = new Book(isbn, title, author);
                    book.setStatus(status);
                    bookList.addBook(book);
                }
            }
            return bookList;
        } catch (IOException e) {
            System.err.println("Error reading the book database: " + e.getMessage());
        }
        return null;
    }

    /**
     * Saves the book list to the database.
     * This method writes the book data to a file.
     * @param bookList The BookList object containing the books to be saved.
     */
    public static void saveBookList(String filePath, BookList bookList) {
        // Save books to the database
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (Map.Entry<String, Book> entry : bookList.getAllBooks().entrySet()) {
                Book book = entry.getValue();
                bw.write(book.getIsbn() + "," + book.getTitle() + "," + book.getAuthor() +
                        "," + book.getStatus());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the book database: " + e.getMessage());
        }
    }

    /**
     * 保存用户偏好设置
     * @param filePath 文件路径
     * @param preferences 用户偏好对象
     */
    public static void saveUserPreferences(String filePath, UserPreferences preferences) {
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream(filePath))) {
            oos.writeObject(preferences);
        } catch (IOException e) {
            System.err.println("Error saving user preferences: " + e.getMessage());
        }
    }

    /**
     * 加载用户偏好设置
     * @param filePath 文件路径
     * @return 用户偏好对象，如果加载失败则返回默认偏好
     */
    public static UserPreferences loadUserPreferences(String filePath) {
        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                new java.io.FileInputStream(filePath))) {
            return (UserPreferences) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Creating new user preferences file.");
            return new UserPreferences(); // 返回默认偏好
        }
    }
}
