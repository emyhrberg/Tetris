import java.awt.*;
import java.util.List;

// A Powerup which pushes other squares down as far as possible
public class Heavy extends AbstractFallHandler {

    @Override protected boolean handleHeavyCollision(List<Point> collidingSquares, SquareType squareType, Board board, int x, int y) {
	if (squareType != SquareType.EMPTY) {
	    collidingSquares.add(new Point(x, y));

	    // Check if all colliding squares can be pushed down
	    for (Point square : collidingSquares)
		if (!canPushColumn(square, board))
		    return true;

	    // Push down all colliding squares
	    for (Point square : collidingSquares)
		pushColumnDown(square, board);

	    return false;
	}
	return false;
    }

    // Returns true if a column can be pushed down.
    private boolean canPushColumn(Point square, Board board) {
	for (int y = square.y + 1; y < board.getHeight(); y++) {
	    if (board.getSquareType(y, square.x) == SquareType.EMPTY) {
		return true; // Column can be pushed down
	    }
	}
	return false; // Column can NOT be pushed down
    }

    //    Identify the highest unoccupied Y-coordinate in the column for potential downward movement.
    //		Note that the highest number means below the colliding squares because Y-coords are calculated starting at top 0, bottom 11
    private void pushColumnDown(Point square, Board board) {
	int highestEmptyY = square.y;
	while (highestEmptyY < board.getHeight() && board.getSquareType(highestEmptyY, square.x) != SquareType.EMPTY) {
	    highestEmptyY++; // Update highest empty Y
	}

	if (highestEmptyY < board.getHeight()) {
	    for (int y = highestEmptyY; y > square.y; y--) {
		board.setSquares(y, square.x, board.getSquareType(y - 1, square.x));
	    }
	    board.setSquares(square.y, square.x, SquareType.EMPTY);
	}
    }

    @Override public String getDescription() {
	return "Heavy";
    }
}
