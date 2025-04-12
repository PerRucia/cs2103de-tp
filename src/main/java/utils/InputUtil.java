package utils;

import java.util.Scanner;

public class InputUtil {
    // Removed 'final' to allow test modifications of the scanner instance.
    private static Scanner scanner = new Scanner(System.in);

    public static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static boolean readYesNo(String prompt) {
        String input = readString(prompt).trim().toLowerCase();
        return input.equals("y") || input.equals("yes");
    }

    public static void close() {
        scanner.close();
    }
} 
