public class BoardTester {

    public static void main(String[] args) {

	// Create a new board and convert the board
	Board board = new Board(4, 6);

	// Shuffle some boards
	for (int i = 0; i < 10; i++) {
	    board.randomizeBoard(); // Generate a random board
	    String boardStr = BoardToTextConverter.convertToText(board); // Convert board to a string
	    System.out.println(boardStr);
	}
    }
}
