package utils.authentication;

import javafx.util.Pair;

import java.io.*;
import java.util.Base64;

public class UserAuth {
    private final String fileName;

    public UserAuth(String fileName) {
        this.fileName = fileName;
    }

    private String xorEncrypt(String password, char key) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : password.toCharArray()) {
            encrypted.append((char) (c ^ key)); // XOR each character with key
        }
        return Base64.getEncoder().encodeToString(encrypted.toString().getBytes());
    }
    
    private String xorDecrypt(String encryptedPassword, char key) {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
        String decodedString = new String(decodedBytes);
        StringBuilder decrypted = new StringBuilder();
        for (char c : decodedString.toCharArray()) {
            decrypted.append((char) (c ^ key)); // XOR again to decrypt
        }
        return decrypted.toString();
    }

    // Register a new user
    public String registerUser(String username, String password) {
        Pair<Boolean, String> checkUsername = validUsername(username);
        if (!checkUsername.getKey()) {
            return checkUsername.getValue(); // Return error message if username is invalid
        }

        Pair<Boolean, String> checkPassword = validPassword(password);
        if (!checkPassword.getKey()) {
            return checkPassword.getValue(); // Return error message if password is invalid
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            String encryptedPassword = xorEncrypt(password, 'K');
            writer.write(username + "|" + encryptedPassword);
            writer.newLine();
            return "User registered successfully!";
        } catch (IOException e) {
            return "Error registering user: " + e.getMessage();
        }
    }

    /**
     * Check if password is valid.
     * @param password The password to check.
     * @return A Pair containing a boolean indicating validity and an error message if invalid.
     */
    private Pair<Boolean, String> validPassword(String password) {
        if (password == null || password.isEmpty()) {
            return new Pair<>(false, "Password cannot be null or empty.");
        } else if (password.length() < 8) {
            return new Pair<>(false, "Password must be at least 8 characters long.");
        } else if (password.length() > 20) {
            return new Pair<>(false, "Password must be at most 20 characters long.");
        } else if (!password.matches(".*[A-Z].*")) {
            return new Pair<>(false, "Password must contain at least one uppercase letter.");
        } else if (!password.matches(".*[a-z].*")) {
            return new Pair<>(false, "Password must contain at least one lowercase letter.");
        } else if (password.contains(" ") || password.contains("|")) {
            return new Pair<>(false, "Password cannot contain spaces or | character.");
        }

        return new Pair<>(true, "Password is valid");
    }
    /**
     * Check if username is valid.
     * @param username The username to check.
     * @return A Pair containing a boolean indicating validity and an error message if invalid.
     */
    private Pair<Boolean, String> validUsername(String username) {
        if (username == null || username.isEmpty()) {
            return new Pair<>(false, "Username cannot be null or empty.");
        } else if (userExists(username)) {
            return new Pair<>(false, "Username already in use.");
        } else if (username.length() < 3) {
            return new Pair<>(false, "Username must be at least 3 characters long.");
        } else if (username.length() > 20) {
            return new Pair<>(false, "Username must be at most 20 characters long.");
        } else if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            return new Pair<>(false, "Username can only contain letters, digits, dots, " +
                    "underscores, and hyphens.");
        }
        return new Pair<>(true, "Username is valid");
    }

    /**
     * Check if user exists in the file.
     * @param username The username to check.
     * @return true if user exists, false otherwise.
     */
    private boolean userExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData.length == 2 && userData[0].equals(username)) {
                    return true; // User already exists
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
        return false;
    }

    // Check if user exists and password matches
    public boolean authenticateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split("\\|");
                if (userData.length == 2 && userData[0].equals(username)) {
                    String decryptedPassword = xorDecrypt(userData[1], 'K');
                    return decryptedPassword.equals(password);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
        return false;
    }
}
