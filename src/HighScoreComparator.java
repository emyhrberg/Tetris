import java.util.Comparator;

public class HighScoreComparator implements Comparator<HighScore> {
    @Override public int compare(final HighScore o1, final HighScore o2) {
	return Integer.compare(o2.getScore(), o1.getScore());
    }
}
