package utils.authentication;

import java.io.*;
import java.util.Base64;
import java.util.Scanner;

public class UserAuth {
    private static final String FILE_NAME = "data/users.txt";

    private static String xorEncrypt(String password, char key) {
        StringBuilder encrypted = new StringBuilder();
        for (char c : password.toCharArray()) {
            encrypted.append((char) (c ^ key)); // XOR each character with key
        }
        return Base64.getEncoder().encodeToString(encrypted.toString().getBytes());
    }
    
    private static String xorDecrypt(String encryptedPassword, char key) {
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedPassword);
        String decodedString = new String(decodedBytes);
        StringBuilder decrypted = new StringBuilder();
        for (char c : decodedString.toCharArray()) {
            decrypted.append((char) (c ^ key)); // XOR again to decrypt
        }
        return decrypted.toString();
    }

    // Register a new user
    public static void registerUser(String username, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            String encryptedPassword = xorEncrypt(password, 'K'); // Simple XOR encryption with a key
            writer.write(username + "," + encryptedPassword);
            writer.newLine();
            System.out.println("User registered successfully!");
        } catch (IOException e) {
            System.out.println("Error registering user: " + e.getMessage());
        }
    }

    // Check if user exists and password matches
    public static boolean authenticateUser(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] userData = line.split(",");
                if (userData.length == 2 && userData[0].equals(username)) {
                    String decryptedPassword = xorDecrypt(userData[1], 'K'); // Decrypt the stored password
                    return decryptedPassword.equals(password);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading user data: " + e.getMessage());
        }
        return false;
    }
}
