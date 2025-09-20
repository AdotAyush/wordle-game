import java.io.*;
import java.time.LocalDate;
import java.util.*;

class User {
    private String username;
    private String password;
    private String role;
    
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}

class GameResult {
    private String username;
    private String word;
    private boolean won;
    private int guesses;
    private LocalDate date;
    
    public GameResult(String username, String word, boolean won, int guesses, LocalDate date) {
        this.username = username;
        this.word = word;
        this.won = won;
        this.guesses = guesses;
        this.date = date;
    }
    
    public String getUsername() { return username; }
    public String getWord() { return word; }
    public boolean isWon() { return won; }
    public int getGuesses() { return guesses; }
    public LocalDate getDate() { return date; }
}

public class WordGuessingGame {
    
    private static final String[] WORDS = {
        "APPLE", "BRAIN", "CHAIR", "DANCE", "EARTH",
        "FRUIT", "GRASS", "HORSE", "IDEAS", "JUICE",
        "KNIFE", "LIGHT", "MUSIC", "NIGHT", "OCEAN",
        "PLANT", "QUEEN", "RIVER", "SMART", "TIGER"
    };
    
    private static final int MAX_GUESSES_PER_GAME = 5;
    private static final int MAX_GAMES_PER_DAY = 3;
    
    private static final String USERS_FILE = "users.txt";
    private static final String GAMES_FILE = "games.txt";
    
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
        System.out.println("=== WORD GUESSING GAME ===");
        System.out.println("Welcome to the Word Guessing Game!");
        
        while (true) {
            showMainMenu();
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    register();
                    break;
                case 2:
                    login();
                    break;
                case 3:
                    if (currentUser != null) {
                        playGame();
                    } else {
                        System.out.println("Please login first!");
                    }
                    break;
                case 4:
                    if (currentUser != null && currentUser.getRole().equals("ADMIN")) {
                        showAdminMenu();
                    } else {
                        System.out.println("Admin access required!");
                    }
                    break;
                case 5:
                    if (currentUser != null) {
                        logout();
                    }
                    break;
                case 6:
                    System.out.println("Thank you for playing! Goodbye!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        }
    }
    
    private static void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Register");
        System.out.println("2. Login");
        System.out.println("3. Play Game");
        System.out.println("4. Admin Dashboard");
        System.out.println("5. Logout");
        System.out.println("6. Exit");
        if (currentUser != null) {
            System.out.println("\nLogged in as: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");
        }
    }
    
    private static void register() {
        System.out.println("\n=== REGISTRATION ===");
        
        String username = getStringInput("Enter username (min 5 chars, must have upper & lower case): ");
        if (!isValidUsername(username)) {
            System.out.println("Invalid username! Must be at least 5 characters with both upper and lower case letters.");
            return;
        }
        
        if (userExists(username)) {
            System.out.println("Username already exists!");
            return;
        }
        
        String password = getStringInput("Enter password (min 5 chars, must have alpha, numeric, and special char $,%,*,@): ");
        if (!isValidPassword(password)) {
            System.out.println("Invalid password! Must be at least 5 characters with alpha, numeric, and special character ($, %, *, @).");
            return;
        }
        
        String role = getStringInput("Enter role (ADMIN/PLAYER): ").toUpperCase();
        if (!role.equals("ADMIN") && !role.equals("PLAYER")) {
            System.out.println("Invalid role! Must be ADMIN or PLAYER.");
            return;
        }
        
        User newUser = new User(username, password, role);
        saveUser(newUser);
        System.out.println("Registration successful!");
    }
    
    private static void login() {
        System.out.println("\n=== LOGIN ===");
        
        String username = getStringInput("Enter username: ");
        String password = getStringInput("Enter password: ");
        
        User user = authenticateUser(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + username + "!");
        } else {
            System.out.println("Invalid username or password!");
        }
    }
    
    private static void playGame() {
        System.out.println("\n=== PLAY GAME ===");
        
        int todayGames = getTodayGameCount(currentUser.getUsername());
        if (todayGames >= MAX_GAMES_PER_DAY) {
            System.out.println("You have reached the daily limit of " + MAX_GAMES_PER_DAY + " games!");
            return;
        }
        
        String targetWord = WORDS[new Random().nextInt(WORDS.length)];
        System.out.println("Game started! You have " + MAX_GUESSES_PER_GAME + " guesses.");
        System.out.println("Guess a 5-letter word (uppercase only):");
        
        boolean gameWon = false;
        int guessCount = 0;
        
        while (guessCount < MAX_GUESSES_PER_GAME && !gameWon) {
            System.out.print("Guess " + (guessCount + 1) + ": ");
            String guess = scanner.nextLine().toUpperCase().trim();
            
            if (guess.length() != 5) {
                System.out.println("Please enter exactly 5 letters!");
                continue;
            }
            
            if (!guess.matches("[A-Z]{5}")) {
                System.out.println("Please enter only uppercase letters!");
                continue;
            }
            
            guessCount++;
            
            // Check if guess is correct
            if (guess.equals(targetWord)) {
                gameWon = true;
                System.out.println("🎉 Congratulations! You guessed the word correctly!");
                System.out.println("The word was: " + targetWord);
            } else {
                // Show feedback
                showGuessFeedback(guess, targetWord);
                
                if (guessCount >= MAX_GUESSES_PER_GAME) {
                    System.out.println("😔 Better luck next time! The word was: " + targetWord);
                }
            }
        }
        
        // Save game result
        saveGameResult(currentUser.getUsername(), targetWord, gameWon, guessCount);
    }
    
    private static void showGuessFeedback(String guess, String targetWord) {
        System.out.print("Feedback: ");
        
        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            char targetChar = targetWord.charAt(i);
            
            if (guessChar == targetChar) {
                System.out.print("🟩"); 
            } else if (targetWord.contains(String.valueOf(guessChar))) {
                System.out.print("🟨"); 
            } else {
                System.out.print("⬜"); 
            }
        }
        System.out.println();
    }
    
    private static void showAdminMenu() {
        while (true) {
            System.out.println("\n=== ADMIN DASHBOARD ===");
            System.out.println("1. Daily Report");
            System.out.println("2. User Report");
            System.out.println("3. Back to Main Menu");
            
            int choice = getIntInput("Enter your choice: ");
            
            switch (choice) {
                case 1:
                    showDailyReport();
                    break;
                case 2:
                    showUserReport();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
    
    private static void showDailyReport() {
        System.out.println("\n=== DAILY REPORT ===");
        String dateStr = getStringInput("Enter date (YYYY-MM-DD) or press Enter for today: ");
        
        LocalDate date;
        if (dateStr.isEmpty()) {
            date = LocalDate.now();
        } else {
            try {
                date = LocalDate.parse(dateStr);
            } catch (Exception e) {
                System.out.println("Invalid date format!");
                return;
            }
        }
        
        List<GameResult> games = loadGameResults();
        int totalUsers = 0;
        int correctGuesses = 0;
        Set<String> users = new HashSet<>();
        
        for (GameResult game : games) {
            if (game.getDate().equals(date)) {
                users.add(game.getUsername());
                if (game.isWon()) {
                    correctGuesses++;
                }
            }
        }
        
        totalUsers = users.size();
        
        System.out.println("\nReport for " + date + ":");
        System.out.println("Total Users: " + totalUsers);
        System.out.println("Correct Guesses: " + correctGuesses);
    }
    
    private static void showUserReport() {
        System.out.println("\n=== USER REPORT ===");
        String username = getStringInput("Enter username: ");
        
        List<GameResult> games = loadGameResults();
        Map<LocalDate, Integer> wordsTried = new HashMap<>();
        Map<LocalDate, Integer> correctGuesses = new HashMap<>();
        
        for (GameResult game : games) {
            if (game.getUsername().equals(username)) {
                LocalDate date = game.getDate();
                wordsTried.put(date, wordsTried.getOrDefault(date, 0) + 1);
                if (game.isWon()) {
                    correctGuesses.put(date, correctGuesses.getOrDefault(date, 0) + 1);
                }
            }
        }
        
        if (wordsTried.isEmpty()) {
            System.out.println("No games found for user: " + username);
            return;
        }
        
        System.out.println("\nReport for " + username + ":");
        System.out.println("Date\t\tWords Tried\tCorrect Guesses");
        System.out.println("----------------------------------------");
        
        for (LocalDate date : wordsTried.keySet()) {
            int tried = wordsTried.get(date);
            int correct = correctGuesses.getOrDefault(date, 0);
            System.out.println(date + "\t" + tried + "\t\t" + correct);
        }
    }
    
    private static void logout() {
        currentUser = null;
        System.out.println("Logged out successfully!");
    }
    
    private static boolean isValidUsername(String username) {
        return username.length() >= 5 && 
               username.matches(".*[a-z].*") && 
               username.matches(".*[A-Z].*");
    }
    
    private static boolean isValidPassword(String password) {
        return password.length() >= 5 && 
               password.matches(".*[a-zA-Z].*") && 
               password.matches(".*[0-9].*") && 
               password.matches(".*[\\$%*@].*");
    }
    
    private static boolean userExists(String username) {
        List<User> users = loadUsers();
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }
    
    private static User authenticateUser(String username, String password) {
        List<User> users = loadUsers();
        return users.stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }
    
    private static int getTodayGameCount(String username) {
        List<GameResult> games = loadGameResults();
        LocalDate today = LocalDate.now();
        return (int) games.stream()
                .filter(g -> g.getUsername().equals(username) && g.getDate().equals(today))
                .count();
    }
    
    private static void saveUser(User user) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE, true))) {
            writer.println(user.getUsername() + "," + user.getPassword() + "," + user.getRole());
        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
        }
    }
    
    private static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return users;
    }
    
    private static void saveGameResult(String username, String word, boolean won, int guesses) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(GAMES_FILE, true))) {
            writer.println(username + "," + word + "," + won + "," + guesses + "," + LocalDate.now());
        } catch (IOException e) {
            System.out.println("Error saving game result: " + e.getMessage());
        }
    }
    
    private static List<GameResult> loadGameResults() {
        List<GameResult> games = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(GAMES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    games.add(new GameResult(parts[0], parts[1], 
                            Boolean.parseBoolean(parts[2]), 
                            Integer.parseInt(parts[3]), 
                            LocalDate.parse(parts[4])));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet, return empty list
        }
        return games;
    }
    
    private static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
}

