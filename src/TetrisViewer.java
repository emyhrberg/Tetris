import javax.swing.*;
import java.awt.*;

public class TetrisViewer {
    // Variables
    private JFrame frame = new JFrame("Tetris");
    private TetrisGame game;
    private TetrisComponent tetrisComponent = null;
    private Board board = null;
    private int boardWidth;
    private int boardHeight;

    // Menu
    private final JMenuItem quit = new JMenuItem("Quit", 'Q');

    // Dimensions of window of the game
    private static final int WINDOW_WIDTH = 1600;
    private static final int WINDOW_HEIGHT = 1000;

    // Text
    private JLabel scoreLabel = new JLabel();
    private JTextPane highscoreTextPane = new JTextPane();
    private JLabel polyLabel = new JLabel();
    private JLabel speedLabel = new JLabel();

    // Constructor
    public TetrisViewer(final TetrisGame game, final Board board, final int boardWidth, final int boardHeight) {
	this.game = game;
	this.board = board;
	this.boardWidth = boardWidth;
	this.boardHeight = boardHeight;

	// Menu
	JMenuBar menuBar = new JMenuBar();
	JMenu options = new JMenu("Options");
	menuBar.add(options);
	JMenuItem pause = new JMenuItem("Pause", 'P');
	options.add(pause);
	JMenuItem resume = new JMenuItem("Resume", 'R');
	options.add(resume);
	options.add(quit);

	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // stop game if user closes
	frame.setJMenuBar(menuBar);

	// Handle actions when pressing stuff in menu
	quitAction();
	pause.addActionListener(event -> board.setGamePause(true));
	resume.addActionListener(event -> board.setGamePause(false));
    }

    private void quitAction() {
	quit.addActionListener(event -> {
	    if (JOptionPane.showConfirmDialog(null, "Quit?", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
		System.exit(0);
	});
    }

    public void show() {
	// Set layout, background color and center the frame
	frame.setLayout(null);
	frame.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
	frame.getContentPane().setBackground(new Color(24, 22, 24));
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	int centerX = (int) (screenSize.getWidth() / 2 - (WINDOW_WIDTH / 2));
	frame.setBounds(centerX, 0, WINDOW_WIDTH, WINDOW_HEIGHT);

	// Text elements position
	final int tetrisWidth = boardWidth * TetrisComponent.BLOCK_SIZE;
	final int tetrisHeight = boardHeight * TetrisComponent.BLOCK_SIZE;
	final int topY = 30; // distance from top
	final int height = 70;
	final int scoreLabelPadding = 90;
	final int labelY = topY + height;
	final int scoreLabelY = topY + scoreLabelPadding;
	final int scoreWidth = 300;
	final int scoreX = WINDOW_WIDTH / 2 - scoreWidth / 2;
	final int highscoreX = scoreX - tetrisWidth;
	final int highscoreY = 300;
	final int paddingX = 200;
	final int polyX = scoreX - paddingX;
	final int speedX = scoreX + paddingX;

	// Tetris position
	final int tetrisY = labelY + scoreLabelPadding;
	final int tetrisX = (WINDOW_WIDTH/2) - tetrisWidth / 2;

	// Add tetris component(actual game) in the center
	tetrisComponent = new TetrisComponent(board, frame);
	tetrisComponent.setBounds(tetrisX, tetrisY, tetrisWidth, tetrisHeight);
	frame.add(tetrisComponent);

	// Score
	JLabel scoreHeader = createLabel(scoreX, topY, scoreWidth, height, "Score", new Font("Verdana", Font.PLAIN, 70), new Color(242, 255, 255), JLabel.CENTER, frame);
	scoreLabel = createLabel(scoreX, labelY, scoreWidth, height, "", new Font("Verdana", Font.PLAIN, 60), new Color(55, 234, 55), JLabel.CENTER, frame);

	// Speed
	JLabel speedHeader = createLabel(speedX, topY, scoreWidth, height, "Speed", new Font("Verdana", Font.PLAIN, 40), new Color(240, 255, 255), JLabel.CENTER, frame);
	speedLabel = createLabel(speedX, labelY, scoreWidth, height, "", new Font("Verdana", Font.PLAIN, 25), new Color(243, 66, 66), JLabel.CENTER, frame);

	// Poly
	JLabel polyHeader = createLabel(polyX, topY, scoreWidth, height, "Poly", new Font("Verdana", Font.PLAIN, 40), new Color(239, 255, 255), JLabel.CENTER, frame);
	polyLabel = createLabel(polyX, labelY, scoreWidth, height, "Heavy", new Font("Verdana", Font.PLAIN, 25), new Color(255, 255, 255), JLabel.CENTER, frame);

	// High Scores
	JLabel highscoreHeader = createLabel(highscoreX, highscoreY, scoreWidth, height, "High Scores", new Font("Verdana", Font.PLAIN, 40), new Color(241, 255, 255), JLabel.LEFT, frame);
	highscoreTextPane.setFont(new Font("Verdana", Font.PLAIN, 25));
	highscoreTextPane.setForeground(new Color(217, 39, 217));
	highscoreTextPane.setBackground(new Color(24, 22, 24));
	highscoreTextPane.setBounds(highscoreX, highscoreY + height, scoreWidth, tetrisHeight);
	highscoreTextPane.setEditable(false);
	highscoreTextPane.setFocusable(false);
	frame.add(highscoreTextPane);

	// Finally, show the frame
	frame.pack();
	frame.setVisible(true);
    }

    private JLabel createLabel(int x, int y, int width, int height, String text, Font font, Color color, int alignment, Container container) {
	JLabel label = new JLabel(text);
	label.setFont(font);
	label.setForeground(color);
	label.setBounds(x, y, width, height);
	label.setHorizontalAlignment(alignment);
	container.add(label);
	return label;
    }

    public void updateLabels() { // updates current score and highscores, called every tick
	// Update current score
	int scoreNumber = board.getScore();
	scoreLabel.setText(String.valueOf(scoreNumber));

	// Update high score
	StringBuilder highScores = buildHighScoreList();
	highscoreTextPane.setText(String.valueOf(highScores));

	// Update speed
	final int defaultGameSpeed = 500;
	final int currentGameSpeed = game.getCurrentGameSpeed();
	float gameSpeedFactor = (float) defaultGameSpeed / currentGameSpeed;
	speedLabel.setText(String.format("%.2fx", gameSpeedFactor));

	// Update poly type color and text
	String polyType = board.getPolyType();
	if (polyType.equals("Heavy")) {
	    polyLabel.setForeground(new Color(208, 255, 0));
	} else if (polyType.equals("Fall Through")) {
	    polyLabel.setForeground(new Color(0, 255, 233));
	} else {
	    polyLabel.setForeground(new Color(255, 255, 255));
	}
	polyLabel.setText(polyType);
    }

    public void update() {
	updateLabels();
	tetrisComponent.boardChanged();
	frame.revalidate();
    }

    private StringBuilder buildHighScoreList() {
	StringBuilder builder = new StringBuilder();
	if (game.getHighScoreList().getHighScores().isEmpty()) {
	    builder.append("No highscores yet!");
	} else {
	    for (HighScore highScore : game.getHighScoreList().getHighScores()) { // Builds high score strings followed by new line
		builder.append(highScore.getUsername()).append(": ").append(highScore.getScore()).append("\n"); //User1: <score>
	    }
	}
	return builder;
    }

    public JFrame getFrame() {
	return frame;
    }
}
