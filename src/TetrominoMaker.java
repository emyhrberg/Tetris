public class TetrominoMaker {
    // Returns the total number of possible polys
    public int getNumberOfTypes() {
	return 7;
    }

    // Creates polys of different square-types.
    public Poly getPoly(int n) {
	return switch(n) {
	    case 0 -> getI();
	    case 1 -> getO();
	    case 2 -> getT();
	    case 3 -> getS();
	    case 4 -> getZ();
	    case 5 -> getJ();
	    case 6 -> getL();
	    default -> throw new IllegalArgumentException("Invalid index: " + n);
	};
    }

    // Create each poly
    private Poly getI() { // 4X4 Array
	return new Poly(new SquareType[][] {
		{SquareType.EMPTY, SquareType.EMPTY, SquareType.I, SquareType.EMPTY},
		{SquareType.EMPTY, SquareType.EMPTY, SquareType.I, SquareType.EMPTY},
		{SquareType.EMPTY, SquareType.EMPTY, SquareType.I, SquareType.EMPTY},
		{SquareType.EMPTY, SquareType.EMPTY, SquareType.I, SquareType.EMPTY}});
    }

    private Poly getO() { // 2X2 Array
	return new Poly(new SquareType[][] {
		{SquareType.O, SquareType.O},
		{SquareType.O, SquareType.O}});
    }

    private Poly getT() { // 3X3 Array
	return new Poly(new SquareType[][] {
		{SquareType.EMPTY, 	SquareType.T, 		SquareType.EMPTY},
		{SquareType.T, 		SquareType.T, 		SquareType.T},
		{SquareType.EMPTY, 	SquareType.EMPTY, 	SquareType.EMPTY}});
    }

    private Poly getS() { // 3X3 Array
	return new Poly(new SquareType[][] {
		{SquareType.EMPTY, 	SquareType.S, 		SquareType.S},
		{SquareType.S, 		SquareType.S, 		SquareType.EMPTY},
		{SquareType.EMPTY, 	SquareType.EMPTY, 	SquareType.EMPTY}});
    }

    private Poly getZ() { // 3X3 Array
	return new Poly(new SquareType[][] {
		{SquareType.Z, 		SquareType.Z, 		SquareType.EMPTY},
		{SquareType.EMPTY, 	SquareType.Z, 		SquareType.Z},
		{SquareType.EMPTY, 	SquareType.EMPTY, 	SquareType.EMPTY}});
    }

    private Poly getJ() { // 3X3 Array
	return new Poly(new SquareType[][] {
		{SquareType.EMPTY, 	SquareType.J, 		SquareType.J},
		{SquareType.EMPTY, 	SquareType.J, 		SquareType.EMPTY},
		{SquareType.EMPTY, 	SquareType.J, 		SquareType.EMPTY}});
    }

    private Poly getL() { // 3X3 Array
	return new Poly(new SquareType[][] {
		{SquareType.EMPTY, 	SquareType.L, 		SquareType.EMPTY},
		{SquareType.EMPTY, 	SquareType.L, 		SquareType.EMPTY},
		{SquareType.EMPTY, 	SquareType.L, 		SquareType.L}});
    }
}
