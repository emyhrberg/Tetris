import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class AbstractFallHandler implements FallHandler {

    @Override public boolean hasCollision(final Board board) {
	List<Point> collidingSquares = new ArrayList<>();
	for (int y = 0; y < board.getPoly().getWidth(); y++) {
	    for (int x = 0; x < board.getPoly().getHeight(); x++) {
		// empty poly -> continue
		SquareType polyType = board.getPoly().getSquareType(y, x);
		if (polyType == SquareType.EMPTY)
		    continue;

		// check the board
		int boardX = x + board.getPolyPosition().x;
		int boardY = y + board.getPolyPosition().y;
		SquareType squareType = board.getSquareType(boardY, boardX);

		// superclass implementation of square collisions
		// is handled in child classes for specific collision scenarios
		if (handleSquareCollision(squareType) ||
		    handleHeavyCollision(collidingSquares, squareType, board, boardX, boardY))
		    return true;
	    }
	}
	return false;
    }

    // Default superclass implementation of handling collisions
    // If a poly is outside the map, it will have a collision
    protected boolean handleSquareCollision(SquareType poly) {
	return poly == SquareType.OUTSIDE;
    }

    // This method returns false by default so it has no effect on all polys except heavy ones.
    // We implement it later in heavy class
    protected boolean handleHeavyCollision(List<Point> collidingSquares, SquareType squareType, Board board, int x, int y) {
	return false;
    }

    @Override public String getDescription() {
	return null;
    }
}
