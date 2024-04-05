public class HighScore {
    // Variables
    private String username;
    private int score;

    // Constructor
    public HighScore(final String username, final int score) {
        this.username = username;
        this.score = score;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }
}
