import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.EnumMap;
import java.util.Map;

public class TetrisComponent extends JComponent implements BoardListener {
    // Variables
    public static final int BLOCK_SIZE = 60;
    private final Board board;

    // No magic numbers: fix variables
    private final static double SCALE_NUMBER = 0.8;
    private final static int BORDER_SIZE = 2;

    // Constructor
    public TetrisComponent(Board board, JFrame frame) {
	this.board = board;
	this.board.addBoardListener(this);
	JComponent pane = frame.getRootPane();

	final InputMap inputMap = pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	inputMap.put(KeyStroke.getKeyStroke("UP"),"up");
	inputMap.put(KeyStroke.getKeyStroke("DOWN"),"down");
	inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left");
	inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right");

	final ActionMap actionMap = pane.getActionMap();
	actionMap.put("up", new RotateAction(Direction.RIGHT));
	actionMap.put("down", new RotateAction(Direction.LEFT));
	actionMap.put("left", new MoveAction(Direction.LEFT));
	actionMap.put("right", new MoveAction(Direction.RIGHT));

	// toggle pause with "P"
	inputMap.put(KeyStroke.getKeyStroke("P"), "pause");
	actionMap.put("pause", new PauseAction());
    }

    public Dimension getPreferredSize() {
	double scale = SCALE_NUMBER;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	screenSize.height = (int) (screenSize.getHeight() * scale);
	screenSize.width = (int) (screenSize.getWidth() * scale);
	return screenSize;
    }

    private static EnumMap<SquareType, Color> createColorMap() {
	Color bgColor = new Color(45,42,45); // background color for empty squares

	EnumMap<SquareType, Color> squareTypeColors = new EnumMap<>(Map.ofEntries(
		Map.entry(SquareType.EMPTY, bgColor),
		Map.entry(SquareType.I, new Color(238, 43, 43)),
		Map.entry(SquareType.O, new Color(255, 127, 0)),
		Map.entry(SquareType.T, new Color(255, 255, 0)),
		Map.entry(SquareType.S, new Color(85, 255, 0)),
		Map.entry(SquareType.Z, new Color(24, 132, 255)),
		Map.entry(SquareType.J, new Color(102, 0, 255)),
		Map.entry(SquareType.L, new Color(255, 41, 166))));

	return squareTypeColors;
    }

    @Override protected void paintComponent(Graphics g) {
	super.paintComponent(g);
	final Graphics2D g2d = (Graphics2D) g;

	// Create color map
	EnumMap<SquareType, Color> colors = createColorMap();

	// Define border color and size
	Color borderColor = Color.BLACK;
	final int borderSize = 2;

	// Go through all squares in the board
	for (int y = 0; y < board.getHeight(); y++) {
	    for (int x = 0; x < board.getWidth(); x++) {

		// Get the squaretype
		SquareType type = board.getVisibleSquareAt(x, y);
		g2d.setColor(colors.get(type));
		g2d.fillRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);

		// Draw a border around each square
		g2d.setColor(borderColor);
		g2d.setStroke(new BasicStroke(borderSize));
		g2d.drawRect(x * BLOCK_SIZE, y * BLOCK_SIZE, BLOCK_SIZE, BLOCK_SIZE);
	    }
	}
    }

    @Override public void boardChanged() {
	repaint();
    }

    private class MoveAction extends AbstractAction {
	private final Direction moveDirection;
	private MoveAction(Direction moveDirection) {
	    this.moveDirection = moveDirection;
	}

	@Override public void actionPerformed(final ActionEvent e) {
	    board.move(moveDirection);
	}
    }

    private class RotateAction extends AbstractAction {
	private final Direction rotateDirection;
	private RotateAction(Direction rotateDirection){
	    this.rotateDirection = rotateDirection;
	}

	@Override public void actionPerformed(final ActionEvent e) {
	    board.rotate(rotateDirection);
	}
    }

    private class PauseAction extends AbstractAction {
	@Override public void actionPerformed(ActionEvent e) {
	    board.setGamePause(!board.isGamePause());// Toggle the pause state
	}
    }

}
