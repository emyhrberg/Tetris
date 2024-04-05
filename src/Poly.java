public class Poly {
    // Variables
    private final SquareType[][] poly;
    private final int height;
    private final int width;

    // Constructor
    public Poly(final SquareType[][] poly){
        this.poly = poly;
        this.height = poly.length;
        this.width = poly[0].length;
    }

    // Getters
    public SquareType[][] getPoly() {
        return poly;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
    public SquareType getSquareType(int y, int x) {
        return poly[y][x];
    }

//    Returns a poly at a new position after it has rotated left or right
    public Poly rotate(Direction direction) {
        // Create a new poly with the same dimensions as the original poly (a copy)
        Poly polyRotated = new Poly(new SquareType[height][width]);

        // Go through all rows and columns of the poly
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {

                if (direction == Direction.RIGHT) {
                    // Rotate poly right
                    int rotateRowRight = width - 1 - col;
                    int rotateColRight = row;
                    polyRotated.poly[rotateRowRight][rotateColRight] = this.poly[row][col];
                } else {
                    // Rotate poly left
                    int rotateRowLeft = col;
                    int rotateColLeft = height - 1 - row;
                    polyRotated.poly[rotateRowLeft][rotateColLeft] = this.poly[row][col];
                }
            }
        }
        return polyRotated;
    }
}
