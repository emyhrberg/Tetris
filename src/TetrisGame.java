import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class TetrisGame {
    // Dimensions for the game
    private static final int BOARD_HEIGHT = 12; // 12 is recommended for regular play
    private static final int BOARD_WIDTH = 6; // 6 is recommended for regular play

    // Game variables
    private static final int START_SCREEN_TIME = 2220; // time shown for splash screen
    private static final int GAME_SPEED = 500; // game will do one tick per GAME_SPEED
    private final Board tetrisBoard;
    private final TetrisViewer tetrisViewer;
    private int currentGameSpeed;

    // High scores
    private HighScoreList highScoreList = new HighScoreList();
    private HighScoreComparator highScoreComparator = new HighScoreComparator();

    // Constructor
    public TetrisGame() {
	loadHighScoresFromFile(); // Load and read the high scores file
	this.tetrisBoard = new Board(BOARD_HEIGHT, BOARD_WIDTH);
	this.tetrisViewer = new TetrisViewer(this, tetrisBoard, BOARD_WIDTH, BOARD_HEIGHT);
	this.currentGameSpeed = GAME_SPEED; // Initially set to GAME_SPEED
    }

    public void runTetris() {
	// Show splash screen
	 new TetrisStartScreen().showStartScreenForMilliseconds(START_SCREEN_TIME); // Show a start screen for some time

	// Main game loop
	final Action doOneStep = new AbstractAction() {
	    public void actionPerformed(ActionEvent e) {

		// --- TICK AND UPDATE! ---
		tetrisBoard.tick();
		tetrisViewer.update();

		// --- GAME OVER ---
		if (tetrisBoard.isGameOver()) {
		    // Update scores
		    saveUserInputScore();
		    writeHighScoresToFile();
		    tetrisViewer.updateLabels();

		    // Restart game
		    askUserForRestart();
		    currentGameSpeed = GAME_SPEED;
		}

		// --- UPDATE GAME SPEED ---
		if (!tetrisBoard.isGamePause()) {
		    // decrease the current game speed
		    final int decreaseGameSpeed = 1;
		    currentGameSpeed -= decreaseGameSpeed;

		    // after some time, the game has reached max game speed
		    final int maxGameSpeed = 400;
		    if (currentGameSpeed <= maxGameSpeed)
			currentGameSpeed = maxGameSpeed;

		    // update timer with game speed
		    ((Timer) e.getSource()).setDelay(currentGameSpeed);
		}
	    }
	};

	// Create a timer and do one step every 500 ms
	Timer timer = new Timer(GAME_SPEED, doOneStep);
	timer.setCoalesce(true);

	// Start the timer and show the board with TetrisViewer
	timer.start();
	tetrisViewer.show();
    }

    private void askUserForRestart() { // Ask user for restart
	int restart = JOptionPane.showConfirmDialog(null, "Select 'Yes' to start a new game or 'No' to exit the game", "Play again", JOptionPane.YES_NO_OPTION);
	if (restart == JOptionPane.YES_OPTION) {
	    // Clear old frame
	    tetrisBoard.resetGame(); // reset board
	    JFrame frame = tetrisViewer.getFrame();
	    frame.dispose(); // close old frame
	    Container container = frame.getContentPane();
	    container.removeAll();
	    container.revalidate();
	    container.repaint();

	    // open new frame
	    tetrisViewer.show();
	} else
	    System.exit(0); // user wants to exit
    }

    // High Score Functionality
    private HighScoreList loadHighScoresFromFile() {
	Gson gson = new Gson();

	// Convert high score to a JSON object so it can be added to the list of high scores
	try (BufferedReader reader = new BufferedReader(new FileReader("highscores.json"))){
	    JsonArray highScoresArr = gson.fromJson(reader, JsonArray.class);

	    // Handle empty highscores file, return empty high scores
	    if (highScoresArr == null) return highScoreList;

	    // High scores are stored as JSON objects in a JSON array
	    // To extract and load them, get each object (each highscore) [which contains of username and score]
	    for (JsonElement jsonElement : highScoresArr) { // Add the high score to a newhigh score list
		JsonObject highScoreObject = jsonElement.getAsJsonObject();
		highScoreList.addScore(new HighScore(highScoreObject.get("username").getAsString(), highScoreObject.get("score").getAsInt()));
	    }

	    // Return the highscorelist
	    return highScoreList;

	} catch (FileNotFoundException ignored) { // if file doesnt exist
	    return highScoreList; // just create a new empty high score list
	    // Failed to save score, ask user to try again or not
	} catch (IOException e) {
	    int saveScoreTryAgain = JOptionPane.showConfirmDialog(null, "Error: Failed to load score. Try again?", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
	    if (saveScoreTryAgain == JOptionPane.YES_OPTION) {
		loadHighScoresFromFile();
	    } else {
		System.out.println("Failed to save score");
		e.printStackTrace();
	    }
	}
	return null;
    }

    private void writeHighScoresToFile() {
	while (true) {
	    // Convert high scores to json
	    Gson gson = new GsonBuilder().setPrettyPrinting().create();
	    String highScoresJson = gson.toJson(highScoreList.getHighScores());

	    File tempFile = new File("highscores_temp.json");
	    File actualFile = new File("highscores.json");

	    // Write to temp file first
	    try (PrintWriter printWriter = new PrintWriter(new FileWriter(tempFile))) {
		printWriter.print(highScoresJson);
	    } catch (IOException e) {
		// Failed to save score, ask user to try again or not
		int saveScoreTryAgain = JOptionPane.showConfirmDialog(null, "", "Error", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
		if (saveScoreTryAgain == JOptionPane.YES_OPTION) {
		    continue;
		} else {
		    e.printStackTrace();
		}
	    }

	    // If we reached this point:
	    // the temp file was successfully written to.
	    // Now we delete the old file and rename the new one
	    if (actualFile.exists() && !actualFile.delete()) {
		System.out.println("Failed to delete old high scores file");
		return;  // exit the method if deleting old file fails
	    }

	    // Rename the temp file to become the actual high scores file
	    if (!tempFile.renameTo(actualFile)) {
		System.out.println("Failed to rename temp file to high scores file");
	    }

	    return;
	}
    }

    private void saveUserInputScore() {
	// trigger dialog which asks for name
	String name = JOptionPane.showInputDialog(null, "Please enter your name to save your score", "Enter your name", JOptionPane.INFORMATION_MESSAGE);

	// user pressed cancel, dont save their score
	if (name == null)
	    return;

	// user did not enter any name
	if (name.isEmpty())
	    name = "AnonymousUser";

	int score = tetrisBoard.getScore(); // get score

	// Add to high score list
	highScoreList.addScore(new HighScore(name, score)); // add name and score to list
	highScoreList.sort(highScoreComparator); // sort scores
    }

    // Getters

    public HighScoreList getHighScoreList() {
	return highScoreList;
    }

    public int getCurrentGameSpeed() {
	return currentGameSpeed;
    }

	// --- MAIN METHOD TO RUN THE GAME ---

	public static void main(String[] args) {
		new TetrisGame().runTetris(); // Run the game
	}
}
