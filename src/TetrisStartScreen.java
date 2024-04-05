import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TetrisStartScreen {
    // Variables
    private static final int SCREEN_WIDTH = 500;
    private static final int SCREEN_HEIGHT = 400;
    private static final int CENTER_Y_PADDING = 30;
    private JFrame frame = new JFrame("Welcome to Tetris!");

    // Constructor
    public TetrisStartScreen() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // add image panel
        JPanel panel = createPanel(); // Create panel
        if (panel != null)
            frame.getContentPane().add(panel); // Add panel if it's not null

        frame.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = (int) screenSize.getWidth() / 2;
        int centerY = (int) screenSize.getHeight() / 2;
        int x = centerX - (SCREEN_WIDTH / 2);
        int y = CENTER_Y_PADDING; // move it down a few pixels
        frame.setBounds(x, y, SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.setVisible(true);
    }

    private JPanel createPanel() {
        String imagePath = "tetris-logo.png";

        // add logo but only if the image file exists
        try {
            ImageIcon imageIcon = new ImageIcon(ClassLoader.getSystemResource(imagePath));
            JLabel label = new JLabel(imageIcon);
            JPanel panel = new JPanel();
            panel.add(label);
            return panel;
        } catch (NullPointerException e) {
            System.err.println("Error: Image not found\n" + imagePath + "\n");
            return null;
        }
    }

    public void showStartScreenForMilliseconds(int showTime)  {
	try {
	    Thread.sleep(showTime);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
	frame.setVisible(false);
	frame.dispose(); // close the screen
    }

}
