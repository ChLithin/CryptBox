import java.util.Scanner;
import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class MainCLI{
    static {
        // Load the native library
        System.loadLibrary("cryptbox");
    }
    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        HashMap<String, User> users = loadUsers();

        System.out.println("Welcome to CryptBox\n");
        boolean running = true;
        while(running){
            System.out.println("Please choose an option:");
            System.out.println("1. Login");
            System.out.println("2. Create account");
            System.out.println("3. Exit");
            System.out.println();
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            System.out.println();
            switch(choice){
                case 1:
                    System.out.print("Enter your username: ");
                    String username = scanner.next();
                    System.out.print("Enter your password: ");
                    String password = scanner.next();
                    User user = users.get(username);

                    if(user != null && user.getPassword().equals(hashString(password))){

                        System.out.println();
                        System.out.println("Login successful!");
                        System.out.println("Welcome back " + username + ", ");
                        user.FileSystem(scanner,users); 
                        break;
                    }
                    System.out.println("Wrong credentials \n");
                    break;

                case 2:
                    System.out.print("Enter your username: ");
                    String newUsername = scanner.next();
                    System.out.print("Enter your password: ");
                    String newPassword = scanner.next();
                    System.out.print("Enter your email: ");
                    String newEmail = scanner.next();
                    User newUser  = users.get(newUsername);
                    if(newUser != null){
                    
                        System.out.println("Username already exists \n");
                        break;
                    }
                    else{
                        if(isStrongPassword(newPassword)){
                            newUser = new User(newUsername, hashString(newPassword), newEmail);
                            users.put(newUsername,newUser);
                            System.out.println("User added successfully \n");
                        }
                        else{
                            System.out.println();
                        }
                    }
                    break;

                case 3:
                    System.out.println("Exiting CryptBox \n");
                    saveUsers(users);
                    running = false;
                    break;

                default:
                    System.out.println("Invalid option \n");
                    break;
            }
        }
        scanner.close();   
    }
    // Method to save users to a file
    private static void saveUsers(HashMap<String, User> users) {
        try {
            // Use absolute path directly
            File file = new File(Paths.get("").toAbsolutePath().toString() + "/users.ser");
            
            try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file))) {
                out.writeObject(users);
                //System.out.println("User data saved successfully at: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    // Method to load users from a file
    @SuppressWarnings("unchecked")
    private static HashMap<String, User> loadUsers() {
        File file = new File(Paths.get("").toAbsolutePath().toString() + "/users.ser");
        if (!file.exists()) {
            return new HashMap<>();
        }
        
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(Paths.get("").toAbsolutePath().toString() + "/users.ser"))) {
            return (HashMap<String, User>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user data: " + e.getMessage());
            return new HashMap<>();
        }
    }
    public static String hashString(String input) {
        try {
            // Create a MessageDigest instance for SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            // Perform the hashing
            byte[] hashBytes = digest.digest(input.getBytes());

            // Convert the byte array into a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error: SHA-256 algorithm not found.", e);
        }
    }
    public static boolean isStrongPassword(String password) {
        if (password == null || password.isEmpty()) {
            System.out.println("Error: Password cannot be null or empty.");
            return false;
        }

        if (password.length() < 8) {
            System.out.println("Error: Password must be at least 8 characters long.");
            return false;
        }

        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUppercase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowercase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (!Character.isLetterOrDigit(ch)) {
                hasSpecialChar = true;
            }
        }

        if (!hasUppercase) {
            System.out.println("Error: Password must contain at least one uppercase letter.");
            return false;
        }

        if (!hasLowercase) {
            System.out.println("Error: Password must contain at least one lowercase letter.");
            return false;
        }

        if (!hasDigit) {
            System.out.println("Error: Password must contain at least one digit.");
            return false;
        }

        if (!hasSpecialChar) {
            System.out.println("Error: Password must contain at least one special character.");
            return false;
        }

        return true; // Password is strong
    }

}
