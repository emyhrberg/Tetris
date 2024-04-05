import javax.swing.*;
import java.awt.*;

public class TetrisViewerOld {
    // Variables
    private Board board;

    // Constructor
    public TetrisViewerOld(final Board board) {
	this.board = board;
    }

    public void show() {
	// Create frame, layout and size
	JFrame frame = new JFrame("Tetris");
	frame.setLayout(new BorderLayout());
	frame.setPreferredSize(new Dimension(400, 800));

	// Create text area and add text area to frame
	JTextArea textArea = new JTextArea(BoardToTextConverter.convertToText(board), board.getWidth(), board.getHeight());
	textArea.setFont(new Font("Monospaced", Font.PLAIN, 40));
	frame.add(textArea, BorderLayout.CENTER);

	// Show the window
	frame.pack();
	frame.setVisible(true);
    }
}
