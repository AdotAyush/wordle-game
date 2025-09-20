import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordGame {
    private static List<User> users = new ArrayList<>();
    private static List<GameResult> games = new ArrayList<>();
    private static User currentUser = null;
    private static String currentWord = "";
    private static int guessCount = 0;
    private static boolean gameActive = false;

    private static String[] words = {
    "APPLE", "BRAIN", "CHAIR", "DANCE", "EARTH",
    "FRUIT", "GRASS", "HORSE", "IDEAS", "JUICE",
    "KNIFE", "LIGHT", "MONEY", "NORTH", "OCEAN",
    "PLANT", "QUEEN", "ROBOT", "SUGAR", "TRAIN",
    "UNDER", "VIRUS", "WATER", "XENON", "YOUTH",
    "ZEBRA", "CLOUD", "STORM", "FLOOD", "MOUNT",
    "RIVER", "SHINE", "PEARL", "STONE", "LEMON",
    "MANGO", "BERRY", "MUSIC", "WORDS", "HAPPY",
    "SMILE", "CRASH", "BLEND", "TRACE", "CROWN",
    "GLASS", "SHARP", "ROUND", "FLAME", "SHOCK"
};

    private static JLabel[][] gridLabels = new JLabel[6][5];
    private static JTextField guessField;
    private static JLabel statusLabel;
    private static JFrame mainFrame;
    private static CardLayout cardLayout;
    private static JPanel mainCardPanel;

    public static void main(String[] args) {
        loadData();
        createWordleGUI();
    }

    private static void createWordleGUI() {
        mainFrame = new JFrame("WORDLE");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Color bgColor = Color.WHITE;
        Color darkGray = new Color(58, 58, 60);

        mainFrame.getContentPane().setBackground(bgColor);

        cardLayout = new CardLayout();
        mainCardPanel = new JPanel(cardLayout);
        mainCardPanel.setBackground(bgColor);

        JLabel title = new JLabel("WORDLE", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(darkGray);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel loginPanel = createWordleLoginPanel();

        JPanel gamePanel = createGamePanel();

        mainCardPanel.add(loginPanel, "LOGIN");
        mainCardPanel.add(gamePanel, "GAME");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(mainCardPanel, BorderLayout.CENTER);

        mainFrame.add(mainPanel);

        mainFrame.setMinimumSize(new Dimension(500, 700));
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);

        cardLayout.show(mainCardPanel, "LOGIN");
        updateStatus();
    }

    private static JPanel createWordleLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(58, 58, 60));
        panel.add(userLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        passLabel.setForeground(new Color(58, 58, 60));
        panel.add(passLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        roleLabel.setForeground(new Color(58, 58, 60));
        panel.add(roleLabel, gbc);

        gbc.gridx = 1;
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"PLAYER", "ADMIN"});
        roleCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(roleCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton loginBtn = createWordleButton("LOGIN", new Color(83, 141, 78));
        loginBtn.addActionListener(_ -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            login(username, password);
        });

        JButton registerBtn = createWordleButton("REGISTER", new Color(181, 159, 59));
        registerBtn.addActionListener(_ -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String role = (String) roleCombo.getSelectedItem();
            register(username, password, role);
        });

        buttonPanel.add(loginBtn);
        buttonPanel.add(registerBtn);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private static JPanel createWordleGrid() {
        JPanel panel = new JPanel(new GridLayout(6, 5, 5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                JLabel label = new JLabel("", JLabel.CENTER);
                label.setFont(new Font("Arial", Font.BOLD, 24));
                label.setForeground(Color.WHITE);
                label.setBackground(new Color(58, 58, 60));
                label.setOpaque(true);
                label.setBorder(BorderFactory.createLineBorder(new Color(58, 58, 60), 2));
                label.setPreferredSize(new Dimension(60, 60));

                gridLabels[row][col] = label;
                panel.add(label);
            }
        }

        return panel;
    }

    private static JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel gridPanel = createWordleGrid();

        int cellW = gridLabels[0][0].getPreferredSize().width;
        int cellH = gridLabels[0][0].getPreferredSize().height;
        int cols = 5, rows = 6;
        int hgap = 5, vgap = 5;
        int prefW = cols * cellW + (cols - 1) * hgap + 40;
        int prefH = rows * cellH + (rows - 1) * vgap + 40;
        gridPanel.setPreferredSize(new Dimension(prefW, prefH));

        JScrollPane gridScroll = new JScrollPane(
                gridPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        gridScroll.setBorder(null);
        gridScroll.getVerticalScrollBar().setUnitIncrement(16);

        JPanel inputPanel = createInputPanel();

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        statusPanel.setBackground(Color.WHITE);
        statusLabel = new JLabel("Welcome! Please login to start playing");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setForeground(new Color(58, 58, 60));
        statusPanel.add(statusLabel);

        panel.add(statusPanel, BorderLayout.NORTH);
        panel.add(gridScroll, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.SOUTH);

        return panel;
    }

    private static JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        guessField = new JTextField(10);
        guessField.setFont(new Font("Arial", Font.BOLD, 16));
        guessField.setHorizontalAlignment(JTextField.CENTER);
        guessField.setPreferredSize(new Dimension(200, 40));

        guessField.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    submitGuess();
                }
            }
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        });

        JButton submitBtn = createWordleButton("SUBMIT", new Color(83, 141, 78));
        submitBtn.addActionListener(_ -> submitGuess());

        JButton startBtn = createWordleButton("START", new Color(181, 159, 59));
        startBtn.addActionListener(_ -> startGame());

        JButton statsBtn = createWordleButton("STATS", new Color(129, 131, 132));
        statsBtn.addActionListener(_ -> showStatistics());

        JButton logoutBtn = createWordleButton("LOGOUT", new Color(220, 20, 60));
        logoutBtn.addActionListener(_ -> logout());

        panel.add(new JLabel("Guess: "));
        panel.add(guessField);
        panel.add(submitBtn);
        panel.add(startBtn);
        panel.add(statsBtn);
        panel.add(logoutBtn);

        return panel;
    }

    private static JButton createWordleButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(80, 35));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private static void updateStatus() {
        if (currentUser != null) {
            statusLabel.setText("Logged in as: " + currentUser.username + " (" + currentUser.role + ")");
        } else {
            statusLabel.setText("Please login to start playing");
        }
    }

    private static void submitGuess() {
        String guess = guessField.getText().toUpperCase();
        if (guess.length() != 5) {
            JOptionPane.showMessageDialog(null, "Please enter exactly 5 letters!");
            return;
        }
        makeGuess(guess);
        guessField.setText("");
    }

    private static void updateGrid(String guess, String feedback) {
        if (guessCount <= 6) {
            for (int i = 0; i < 5; i++) {
                JLabel label = gridLabels[guessCount - 1][i];
                label.setText(String.valueOf(guess.charAt(i)));

                if (feedback.charAt(i) == 'G') {
                    label.setBackground(new Color(83, 141, 78));
                } else if (feedback.charAt(i) == 'Y') {
                    label.setBackground(new Color(181, 159, 59));
                } else {
                    label.setBackground(new Color(129, 131, 132));
                }
            }
        }
    }

    private static void login(String username, String password) {
        for (User user : users) {
            if (user.username.equals(username) && user.password.equals(password)) {
                currentUser = user;
                JOptionPane.showMessageDialog(null, "Login successful! Welcome, " + username + "!");
                cardLayout.show(mainCardPanel, "GAME");
                updateStatus();
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Invalid username or password!");
    }

    private static void register(String username, String password, String role) {
        if (username.length() < 3) {
            JOptionPane.showMessageDialog(null, "Username must be at least 3 characters!");
            return;
        }

        for (User user : users) {
            if (user.username.equals(username)) {
                JOptionPane.showMessageDialog(null, "Username already exists!");
                return;
            }
        }

        users.add(new User(username, password, role));
        saveData();
        JOptionPane.showMessageDialog(null, "Registration successful!");
    }

    private static void logout() {
        currentUser = null;
        gameActive = false;
        clearGrid();
        cardLayout.show(mainCardPanel, "LOGIN");
        updateStatus();
        JOptionPane.showMessageDialog(null, "Logged out successfully!");
    }

    private static void startGame() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Please login first!");
            return;
        }

        int todayGames = 0;
        for (GameResult game : games) {
            if (game.username.equals(currentUser.username) && game.date.equals(LocalDate.now())) {
                todayGames++;
            }
        }

        if (todayGames >= 3) {
            JOptionPane.showMessageDialog(null, "You've reached the daily limit of 3 games!");
            return;
        }

        clearGrid();

        Random random = new Random();
        currentWord = words[random.nextInt(words.length)];
        guessCount = 0;
        gameActive = true;

        statusLabel.setText("Game started! You have 6 guesses. Good luck!");
        JOptionPane.showMessageDialog(null, "Game started! Guess a 5-letter word. You have 6 tries!");
    }

    private static void clearGrid() {
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 5; col++) {
                JLabel label = gridLabels[row][col];
                label.setText("");
                label.setBackground(new Color(58, 58, 60));
                label.setForeground(Color.WHITE);
            }
        }
    }

    private static void makeGuess(String guess) {
        if (!gameActive) {
            JOptionPane.showMessageDialog(null, "No active game! Start a new game first.");
            return;
        }

        guessCount++;

        if (guess.equals(currentWord)) {
            gameActive = false;
            games.add(new GameResult(currentUser.username, currentWord, true, guessCount, LocalDate.now()));
            saveData();

            String feedback = getFeedback(guess, currentWord);
            updateGrid(guess, feedback);

            JOptionPane.showMessageDialog(null, "🎉 Congratulations! You guessed it!\nThe word was: " + currentWord);
        } else {
            String feedback = getFeedback(guess, currentWord);
            updateGrid(guess, feedback);

            if (guessCount >= 6) {
                gameActive = false;
                games.add(new GameResult(currentUser.username, currentWord, false, guessCount, LocalDate.now()));
                saveData();
                JOptionPane.showMessageDialog(null, "Game over! The word was: " + currentWord);
            } else {
                statusLabel.setText("Guess " + guessCount + "/6 - Keep trying!");
            }
        }
    }

    private static String getFeedback(String guess, String target) {
        StringBuilder feedback = new StringBuilder();

        for (int i = 0; i < 5; i++) {
            char guessChar = guess.charAt(i);
            char targetChar = target.charAt(i);

            if (guessChar == targetChar) {
                feedback.append("G");
            } else if (target.contains(String.valueOf(guessChar))) {
                feedback.append("Y");
            } else {
                feedback.append("X");
            }
        }

        return feedback.toString();
    }

    private static void showStatistics() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "Please login first!");
            return;
        }

        if (!currentUser.role.equals("ADMIN")) {
            JOptionPane.showMessageDialog(null, "Admin access required!");
            return;
        }

        int totalGames = games.size();
        int totalUsers = users.size();
        int wonGames = 0;
        int todayGames = 0;

        for (GameResult game : games) {
            if (game.won) wonGames++;
            if (game.date.equals(LocalDate.now())) todayGames++;
        }

        String stats = " ADMIN STATISTICS \n\n" +
                "Total Users: " + totalUsers + "\n" +
                "Total Games: " + totalGames + "\n" +
                "Games Won: " + wonGames + "\n" +
                "Win Rate: " + (totalGames > 0 ? (wonGames * 100 / totalGames) + "%" : "0%") + "\n" +
                "Games Today: " + todayGames + "\n\n" +
                "Recent Games:\n";

        int count = 0;
        for (int i = games.size() - 1; i >= 0 && count < 5; i--) {
            GameResult game = games.get(i);
            stats += "• " + game.username + " - " + game.word + " - " +
                    (game.won ? "WON" : "LOST") + " (" + game.guesses + " guesses)\n";
            count++;
        }

        JOptionPane.showMessageDialog(null, stats);
    }
    
    private static void loadData() {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    users.add(new User(parts[0], parts[1], parts[2]));
                }
            }
        } catch (IOException e) {
            // File doesn't exist yet
            System.err.println("Caught exception : " + e);
            e.printStackTrace();
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader("games.txt"))) {
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
            // File doesn't exist yet
            System.err.println("Caught exception : " + e);
            e.printStackTrace();
        }
    }
    
    private static void saveData() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("users.txt"))) {
            for (User user : users) {
                writer.println(user.username + "," + user.password + "," + user.role);
            }
        } catch (IOException e) {
            System.out.println("Error saving users");
        }
        
        try (PrintWriter writer = new PrintWriter(new FileWriter("games.txt"))) {
            for (GameResult game : games) {
                writer.println(game.username + "," + game.word + "," + game.won + "," + game.guesses + "," + game.date);
            }
        } catch (IOException e) {
            System.out.println("Error saving games");
        }
    }
}
