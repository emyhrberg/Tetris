// Powerup that allows the poly to fall through existing squares to the bottom.
public class FallThrough extends AbstractFallHandler {

    @Override protected boolean handleSquareCollision(final SquareType poly) {
	// If the poly is outside, there is a collision.
	return poly == SquareType.OUTSIDE;
    }

    @Override public String getDescription() {
	return "Fall Through";
    }
}
