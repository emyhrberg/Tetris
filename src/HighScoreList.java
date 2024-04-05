import java.util.ArrayList;
import java.util.List;

public class HighScoreList {
    // Variables
    private List<HighScore> highScores = new ArrayList<>();

    // Getters
    public List<HighScore> getHighScores() {
	return highScores;
    }

    // Functions
    public void addScore(HighScore highScore) {
	final int maxScores = 10;
	if (highScores.size() < maxScores)  // max 10 high scores displayed at the same time!
	    highScores.add(highScore);
    }

    public void sort(final HighScoreComparator highScoreComparator) {
	highScores.sort(highScoreComparator);
    }

}
