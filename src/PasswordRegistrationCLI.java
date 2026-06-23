import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.*;

public class PasswordRegistrationCLI {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("--- Secure User Registration System ---");
        System.out.print("Enter the number of users: ");
        int n = sc.nextInt();
        sc.nextLine(); 
        
        String[] name = new String[n];
        String[] dept = new String[n]; 
        String[] hashes = new String[n];
        String[] saltStrings = new String[n]; 
        String[] tempPass = new String[n];

        SecureRandom r = new SecureRandom();

        for(int i = 0; i < n; i++) {
            System.out.println("\n--- Registering User " + (i + 1) + " ---");
            
            System.out.print("Name: ");
            name[i] = sc.nextLine();

            System.out.print("Department: ");
            dept[i] = sc.nextLine();

            String password;
            String strength;
            do {
                System.out.print("Create Password: ");
                password = sc.nextLine();
                
                // Pass the user's name to check for personal/common patterns
                strength = getStrengthLevel(password, name[i]);
                
                System.out.println("Result: " + strength);
                
                if (strength.startsWith("Weak")) {
                    System.out.println("❌ Access Denied: This password is too common or contains your name.");
                    System.out.println("Tip: Avoid '123', your own name, or simple sequences.");
                }
            } while (strength.startsWith("Weak"));

            tempPass[i] = password;

            // Hashing Logic (Same as before)
            byte[] salts = new byte[8];
            r.nextBytes(salts);
            StringBuilder sb = new StringBuilder();
            for (byte b : salts) sb.append(String.format("%02x", b));
            saltStrings[i] = sb.toString();

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest((password + saltStrings[i]).getBytes());
            StringBuilder sbr = new StringBuilder();
            for(byte b : hashBytes) sbr.append(String.format("%02x", b));
            hashes[i] = sbr.toString();
        }

        // ... [Graph logic remains the same as your previous version] ...
        System.out.println("\nRegistration Complete. All passwords secured.");
        sc.close();
    }

    /**
     * Enhanced Strength Logic with Blacklist and Name-Check
     */
    public static String getStrengthLevel(String password, String userName) {
        // 1. Length Check
        if (password.length() < 8) return "Weak (Too Short)";

        // 2. Common Pattern Blacklist
        String[] blacklist = {"123", "password", "qwerty", "admin", "welcome"};
        for (String pattern : blacklist) {
            if (password.toLowerCase().contains(pattern)) {
                return "Weak (Common Pattern: " + pattern + ")";
            }
        }

        // 3. Name-Based Check (Google Behavior)
        // Rejects if password contains the user's name (e.g., Subrat123)
        if (password.toLowerCase().contains(userName.toLowerCase())) {
            return "Weak (Contains your name)";
        }

        // 4. Complexity Scoring
        int score = 0;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[^a-zA-Z0-9].*")) score++;

        if (score >= 4) return "Strong";
        if (score == 3) return "Medium";
        return "Weak (Low Complexity)";
    }
}