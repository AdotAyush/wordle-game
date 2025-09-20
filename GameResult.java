import java.time.LocalDate;

public class GameResult {
    String username;
    String word;
    boolean won;
    int guesses;
    LocalDate date;
    
    public GameResult(String username, String word, boolean won, int guesses, LocalDate date) {
        this.username = username;
        this.word = word;
        this.won = won;
        this.guesses = guesses;
        this.date = date;
    }
}
