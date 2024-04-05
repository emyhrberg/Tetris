import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Board {
    // Board variables
    private SquareType[][] squares;
    private int width, height;
    private List<BoardListener> boardListeners = new ArrayList<>();
    private static final int MARGIN = 2, DOUBLE_MARGIN = MARGIN * 2;
    private boolean gameOver = false, gamePause = false;
    private static final Random RANDOM = new Random();

    // Score
    private int score = 0;
    private final Map<Integer, Integer> pointMap = Map.of(1, 100, 2, 300, 3, 500, 4, 800);

    // Poly
    private Poly poly = null;
    private Point polyPosition = null;
    private FallHandler fallHandler;
    private int polyCount = 0;
    private String polyType = "";

    // Constructor
    public Board(final int height, final int width) {
	this.height = height;
	this.width = width;
	this.squares = new SquareType[height + DOUBLE_MARGIN][width + DOUBLE_MARGIN];
	this.fallHandler = new DefaultFallHandler();

	spawnPoly();
	initSquares(height, width);
    }

//    Tick: main game runs here
    public void tick() {
	if (gamePause) return;

	if (poly == null) {
	    spawnPoly(); // spawn new poly
	    if (polyHasCollision()) // check if poly is colliding
		gameOver = true; // end game if colliding
	    return; // Make sure poly stays for one tick at the top position
	}

	updatePolyPosition(0, 1); // start falling, poly!

	if (polyHasCollision()) {
	    updatePolyPosition(0, -1); // prevent poly from falling below bottom border by moving the poly up by 1 block
	    updateBoardWithPoly(); // update squares with the falling poly
	    poly = null; // set to null so we can spawn a new poly
	}

	updateScore(removeFullRowsFromBoard()); // remove full rows and update the score
	notifyListeners();
    }

//    Returns true if the poly is colliding somewhere in the board
    private boolean polyHasCollision() {
	// handle no poly
	if (poly == null) return false;

	// decide when to spawn powerup or not
	final int fallthrough = 5;
	final int heavy = 2;
	if (polyCount % fallthrough == 0) {
	    // every 2 poly will be powerup; fall through
	    fallHandler = new FallThrough();
	} else if (polyCount % heavy == 0) {
	    // every 5 poly will be powerup; heavy
	    fallHandler = new Heavy();
	} else {
	    // all other are regular polys
	    // first 7 polys will be: reg, heavy, reg, heavy, through, heavy, reg
	    fallHandler = new DefaultFallHandler();
	}
	polyType = fallHandler.getDescription();
	return fallHandler.hasCollision(this);
    }

    // Spawn a new random poly at the top of the game
    private void spawnPoly() {
	polyCount++;

	// spawn a "L" poly.
//	poly = new TetrominoMaker().getPoly(6);

	// spawn a random poly in the middle
	final int removeEmptyAndOutside = 2;
	final int rand = RANDOM.nextInt(SquareType.values().length - removeEmptyAndOutside);
	poly = new TetrominoMaker().getPoly(rand);
	polyPosition = new Point(getWidth() / 2 - poly.getWidth() / 2, 0);
    }

    // Update the polys position. Also handle when poly is moving outside the borders
    public void updatePolyPosition(int x, int y) {
	// update the poly coords by adding to x and y
	polyPosition = new Point(polyPosition.x + x, polyPosition.y + y);

	// poly collided; go back/reset to its previous position
	if (polyHasCollision()) {
	    Point previousPosition = new Point(polyPosition.x - x, polyPosition.y);
	    polyPosition = previousPosition;
	}
    }

    // Update the visible squares with the polys falling position
    public void updateBoardWithPoly() {
	for (int col = polyPosition.y; col < polyPosition.y + poly.getHeight(); col++) {
	    for (int row = polyPosition.x; row < polyPosition.x + poly.getWidth(); row++) {
		squares[col + MARGIN][row + MARGIN] = getVisibleSquareAt(row, col);
	    }
	}
    }

    // Main function to handle full row functionality
    public int removeFullRowsFromBoard() {
	int currentRow = getHeight() + MARGIN - 1;
	int numberOfRowsRemoved = 0;

	while (currentRow >= MARGIN) { // go through each row of the board, starting at the bottom
	    if (rowIsFull(currentRow)) { // current row is full
		removeSingleRow(currentRow);
		numberOfRowsRemoved++;
	    } else {
		currentRow--;
	    }
	}
	return numberOfRowsRemoved;
    }

    // Removes a single given row
    private void removeSingleRow(int row) {
	for (int y = row; y >= MARGIN; y--) { // go through each row ABOVE the removed row
	    if (y == MARGIN) { // skip the TOP and BOTTOM margin and handle OUTSIDE squares, we dont want to shift rows outside the border
		squares[y] = createEmptyRow(y); // create a new empty row at the top
	    } else {
		squares[y] = squares[y - 1]; // move all rows ABOVE the removed row down (y-1)
	    }
	}
    }

    // Creates an empty row on the given y value
    private SquareType[] createEmptyRow(int y){
	SquareType[] emptyRow = new SquareType[width + DOUBLE_MARGIN];

	for (int i = 0; i < emptyRow.length; i++) {
	    // Set outside of the borders of the board to OUTSIDE, and the inside of the borders to EMPTY (inside the board)
	    if (y < MARGIN || i < MARGIN || i >= width + MARGIN || y >= height + MARGIN) {
		emptyRow[i] = SquareType.OUTSIDE;
	    } else {
		emptyRow[i] = SquareType.EMPTY;
	    }
	}
	return emptyRow;
    }

//    Returns true if a given row is full, returns false otherwise
    private boolean rowIsFull(int row) {
	for (SquareType square : squares[row]) { // check an entire row
	    if (square == SquareType.EMPTY) { // if a square OCCUPIES the row, the row is NOT full!
		return false;
	    }
	}
	return true; // otherwise, row is full
    }

//    Moves the poly left or right
    public void move(Direction direction) {
	switch (direction) {
	    case LEFT -> updatePolyPosition(-1, 0); // x goes 1 to the left
	    case RIGHT -> updatePolyPosition(1, 0); // x goes 1 to the right
	}
	notifyListeners();
    }

//    Rotates the poly by calling polyFalling.rotate
    public void rotate(Direction direction) {
	Poly polyFallingReference = poly; // create a reference that points to polyFalling
	Poly polyRotated = null;

	if (poly != null) { // user can only rotate poly when poly actually exists, some errors will be thrown otherwise
	    switch (direction) {
		case RIGHT -> polyRotated = poly.rotate(Direction.RIGHT);
		case LEFT -> polyRotated = poly.rotate(Direction.LEFT);
	    }
	}

	// handle when poly is trying to rotate to an illegal position
	poly = polyRotated;
	if (polyHasCollision()) { // collision: reset the value of polyFalling to the value stored in its reference
	    poly = polyFallingReference;
	}
	notifyListeners();
    }

//    Updates the score with the proper points from pointMap (1 row = 100, 2 rows = 300, etc...)
    private void updateScore(int rowsRemoved) { // add score based on the number of rows removed
	if (rowsRemoved > 0)
	    score += pointMap.get(rowsRemoved);
    }

    // Initialize squares for the board
    private void initSquares(int height, int width) {
	for (int x = 0; x < height + DOUBLE_MARGIN; x++) {
	    for (int y = 0; y < width + DOUBLE_MARGIN; y++) {
		if (isPolyOnBorder(x, y)) {
		    squares[x][y] = SquareType.OUTSIDE;
		} else {
		    squares[x][y] = SquareType.EMPTY;
		}
	    }
	}
    }

    // Returns true if a poly is touches the border at position (x, y), returns false otherwise.
    private boolean isPolyOnBorder(int x, int y) {
	return (x == 0 || x == 1 || x == squares.length - MARGIN || x == squares.length - 1) // poly is touching top or bottom border
	       || (y == 0 || y == 1 || y == squares[0].length - MARGIN || y == squares[0].length - 1); // poly is touching left or right border
    }

//    Reset the game by resetting all variables and creating an empty board
    public void resetGame() {
	gameOver = false;
	score = 0;
	poly = null;
	polyCount = 0;
	polyPosition = null;
	squares = new SquareType[height + DOUBLE_MARGIN][width + DOUBLE_MARGIN];
	initSquares(height, width);
    }

    //    Setters
    public void setGamePause(final boolean gamePause) {
	this.gamePause = gamePause;
    }

    public void setSquares(int y, int x, SquareType squareType) {
	squares[y + MARGIN][x + MARGIN] = squareType;
    }

    //    Getters
    public Poly getPoly() {
	return poly;
    }

    public Point getPolyPosition() {
	return polyPosition;
    }

    public String getPolyType() {
	return polyType;
    }

    public boolean isGamePause() {
	return gamePause;
    }

    public int getScore() {
	return score;
    }

    public boolean isGameOver() {
	return gameOver;
    }

    public int getHeight() {
	return height;
    }

    public int getWidth() {
	return width;
    }

    public SquareType getSquareType(int y, int x) {
	return squares[y + MARGIN][x + MARGIN];
    }

    public SquareType getVisibleSquareAt(int x, int y) {
	// check if x and y coordinate is outside boundaries for falling poly
	if (poly == null ||
	    x > polyPosition.x + poly.getWidth() - 1 ||
	    x < polyPosition.x ||
	    y > polyPosition.y + poly.getHeight() - 1 ||
	    y < polyPosition.y) {
	    return getSquareType(y, x);
	}

	// get the position relative to poly falling position
	int relativeY = y - polyPosition.y;
	int relativeX = x - polyPosition.x;

	// check if falling poly has an empty square at the given coordinates
	if (poly.getSquareType(relativeY, relativeX) == SquareType.EMPTY)
	    return getSquareType(y, x);

	// otherwise, return the relative position for x and y, which is
	// the position of the square in the falling piece relative to the top-left corner of the piece
	return poly.getSquareType(relativeY, relativeX);
    }

    //    Listeners
    public void addBoardListener(BoardListener listener) {
	boardListeners.add(listener);
    }

    private void notifyListeners() {
	for (BoardListener boardListener : boardListeners) {
	    boardListener.boardChanged();
	}
    }

//    Old function used in BoardTester
    public void randomizeBoard() {
	for (int row = 0; row < squares.length; row++) {
	    for (int column = 0; column < squares[row].length; column++) {
		int randomIndex = RANDOM.nextInt(SquareType.values().length - 1);
		squares[row][column] = SquareType.values()[randomIndex];
	    }
	}
    }
}
