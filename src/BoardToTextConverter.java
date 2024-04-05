public class BoardToTextConverter {
    public static String convertToText(Board board) {
	// Create an empty string builder
	StringBuilder str = new StringBuilder();

	// Go through board
	for (int row = 0; row < board.getHeight(); row++) {
	    for (int col = 0; col < board.getWidth(); col++) {
		switch (board.getVisibleSquareAt(col, row)) {
		    case EMPTY:
			str.append("-");
			break;
		    case I:
			str.append("I");
			break;
		    case O:
			str.append("O");
			break;
		    case T:
			str.append("T");
			break;
		    case S:
			str.append("S");
			break;
		    case Z:
			str.append("Z");
			break;
		    case J:
			str.append("J");
			break;
		    case L:
			str.append("L");
			break;
		}
	    }
	    str.append("\n");
	}
	return str.toString();
    }
}
