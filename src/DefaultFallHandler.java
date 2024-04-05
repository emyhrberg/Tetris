public class DefaultFallHandler extends AbstractFallHandler {


    @Override protected boolean handleSquareCollision(final SquareType poly) {
	// If the poly is on a position that is non-empty;
	// There is a collision.
	return poly != SquareType.EMPTY;
    }

    @Override public String getDescription() {
	return "Regular";
    }
}

